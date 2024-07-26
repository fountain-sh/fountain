package sh.fountain.fountain.runtime.config;

import sh.fountain.fountain.api.config.Config;
import sh.fountain.fountain.api.config.ConfigurationProvider;
import sh.fountain.fountain.api.dependency_injection.Injectable;

import com.google.common.reflect.TypeToken;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Injectable
@SuppressWarnings("unchecked") // reflective fun
public class BukkitConfigurationProvider implements ConfigurationProvider {
    private static final List<Class<?>> YAML_PRIMITIVE_TYPES = List.of(Boolean.class, Integer.class, Double.class,
            String.class);

    private final FileResolver fileResolver;

    public BukkitConfigurationProvider(FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    @Override
    public <T> T load(Class<T> configType) {
        final var configMetadata = Optional.ofNullable(configType.getAnnotation(Config.class))
                .orElseThrow(() -> ConfigurationExceptionFactory.missingAnnotation(configType));

        final var config = YamlConfiguration.loadConfiguration(fileResolver.resolve(configMetadata));
        final var root = config.getValues(false);
        final var normalizedRoot = normalizeBukkitYamlRepresentation(root);
        return (T) deriveParser(configType).apply("$", normalizedRoot);
    }

    private BiFunction<String, Object, Object> deriveParser(Type type) {
        final var normalizedType = TypeToken.of(type).wrap().getType();

        if (normalizedType instanceof Class<?> c && YAML_PRIMITIVE_TYPES.contains(c)) {
            return (key, value) -> {
                if (c.isInstance(value)) {
                    return value;

                } else if (value == null) {
                    throw ConfigurationExceptionFactory.missingKey(key, normalizedType);
                } else {
                    throw ConfigurationExceptionFactory.wrongValueTypeForKey(key, value, normalizedType);
                }
            };

        } else if (normalizedType instanceof ParameterizedType t && t.getRawType().equals(List.class)) {
            final var elementType = t.getActualTypeArguments()[0];
            final var elementParser = deriveParser(elementType);

            return (key, value) -> {
                if (value instanceof List<?> list) {
                    final var parsed = new ArrayList<Object>(list.size());
                    for (var i = 0; i < list.size(); i++) {
                        parsed.add(elementParser.apply("%s[%d]".formatted(key, i), list.get(i)));
                    }
                    return parsed;

                } else if (value == null) {
                    return List.of();

                } else {
                    throw ConfigurationExceptionFactory.wrongValueTypeForKey(key, value, normalizedType);
                }
            };

        } else if (normalizedType instanceof ParameterizedType t && t.getRawType().equals(Map.class)) {
            final var mapKeyType = t.getActualTypeArguments()[0];
            final var mapValueType = t.getActualTypeArguments()[1];
            final var mapValueParser = deriveParser(mapValueType);

            return (key, value) -> {

                if (!mapKeyType.equals(String.class)) {
                    throw ConfigurationExceptionFactory.nonStringMapKey(key, normalizedType);
                }

                if (value instanceof Map<?, ?> map) {
                    final var parsed = new HashMap<String, Object>();

                    for (var entry : ((Map<String, Object>) map).entrySet()) {
                        parsed.put(entry.getKey(),
                                mapValueParser.apply("%s.%s".formatted(key, entry.getKey()), entry.getValue()));
                    }

                    return parsed;

                } else if (value == null) {
                    return Map.of();

                } else {
                    throw ConfigurationExceptionFactory.wrongValueTypeForKey(key, value, normalizedType);
                }
            };

        } else if (normalizedType instanceof ParameterizedType t && t.getRawType().equals(Optional.class)) {
            final var valueType = t.getActualTypeArguments()[0];
            final var valueParser = deriveParser(valueType);

            return (key, value) -> value == null ? Optional.empty() : Optional.of(valueParser.apply(key, value));

        } else if (normalizedType instanceof Class<?> c && ConfigurationSerializable.class.isAssignableFrom(c)) {
            return (key, value) -> {
                if (!(value instanceof Map<?, ?> m)) {
                    throw ConfigurationExceptionFactory.wrongValueTypeForKey(key, value, normalizedType);
                }
                final var parsedValue = ConfigurationSerialization.deserializeObject((Map<String, Object>) m,
                        (Class<ConfigurationSerializable>) c);

                if (parsedValue == null) {
                    throw ConfigurationExceptionFactory.configSerializableCallFailed(key, value, normalizedType);
                } else {
                    return parsedValue;
                }
            };

        } else if (normalizedType instanceof Class<?> c) {

            if (!c.isRecord()) {
                throw ConfigurationExceptionFactory.illegalCustomType(normalizedType);
            }

            final var constructor = constructorOf(c).orElseThrow(
                    () -> new IllegalArgumentException("Config type %s must have one public constructor"
                            .formatted(normalizedType.getTypeName())));

            final List<ArgumentInfo> arguments = Arrays.stream(constructor.getParameters())
                    .map(p -> new ArgumentInfo(p.getName(), deriveParser(p.getParameterizedType())))
                    .toList();

            return (key, value) -> {
                if (value instanceof Map<?, ?> map) {

                    final var args = arguments.stream()
                            .map(arg -> arg.parser.apply("%s.%s".formatted(key, arg.key), map.get(arg.key)))
                            .toArray();
                    try {
                        return constructor.newInstance(args);
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException exception) {
                        throw new RuntimeException(exception);
                    }

                } else {
                    throw ConfigurationExceptionFactory.wrongValueTypeForKey(key, value, normalizedType);
                }
            };

        } else {
            assert false;
            throw new IllegalStateException();
        }
    }

    private <T> Optional<Constructor<T>> constructorOf(Class<T> type) {
        final var constructors = (Constructor<T>[]) type.getConstructors();
        return constructors.length == 1 ? Optional.of(constructors[0]) : Optional.empty();
    }

    private record ArgumentInfo(String key, BiFunction<String, Object, Object> parser) {
    }

    private Object normalizeBukkitYamlRepresentation(Object yamlValue) {
        if (yamlValue instanceof List<?> list) {
            return list
                    .stream()
                    .map(this::normalizeBukkitYamlRepresentation).toList();

        } else if (yamlValue instanceof Map<?, ?> map) {
            return map.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Entry::getKey,
                            entry -> normalizeBukkitYamlRepresentation(entry.getValue())));

        } else if (yamlValue instanceof ConfigurationSection section) {
            return section.getKeys(false)
                    .stream()
                    .collect(
                            Collectors.toMap(Function.identity(),
                                    key -> {
                                        final var value = section.get(key);
                                        assert value != null;
                                        return normalizeBukkitYamlRepresentation(value);
                                    }));

        } else {
            return yamlValue;
        }
    }
}
