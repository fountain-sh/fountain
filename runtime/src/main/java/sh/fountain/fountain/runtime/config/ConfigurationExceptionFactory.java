package sh.fountain.fountain.runtime.config;

import sh.fountain.fountain.api.config.Config;
import sh.fountain.fountain.api.config.ConfigurationDeserializationException;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Type;
import java.util.Objects;

public class ConfigurationExceptionFactory {
    private ConfigurationExceptionFactory() {

    }

    private static String stringifyValue(Object value) {
        return value instanceof String s
                ? '"' + s + '"'
                : Objects.toString(value);
    }

    public static ConfigurationDeserializationException missingKey(String key, Type type) {
        return new ConfigurationDeserializationException(
                "Non optional key %s of type %s is missing".formatted(key, type.getTypeName()));
    }

    public static ConfigurationDeserializationException wrongValueTypeForKey(String key, Object value,
            Type expectedType) {
        final var message = "Config key %s has an invalid type. <%s> is not of type %s"
                .formatted(key, stringifyValue(value), expectedType.getTypeName());
        return new ConfigurationDeserializationException(message);
    }

    public static ConfigurationDeserializationException configSerializableCallFailed(String key, Object value,
            Type type) {
        return new ConfigurationDeserializationException(
                "Config key %s could not be deserialized to %s via bukkit using value <%s>"
                        .formatted(key, type.getTypeName(), stringifyValue(value)));
    }

    public static ConfigurationModelException nonStringMapKey(String key, Type type) {
        return new ConfigurationModelException(
                "Config model type %s key %s of type Map must use strings as keys".formatted(key,
                        type.getTypeName()));
    }

    public static ConfigurationModelException missingAnnotation(Type type) {
        return new ConfigurationModelException(
                "Config model type %s must be annotated with %s".formatted(type.getTypeName(),
                        Config.class.getName()));
    }

    public static ConfigurationModelException illegalCustomType(Type type) {
        return new ConfigurationModelException("Custom model type %s must be a record or a subtype of %s"
                .formatted(type.getTypeName(), ConfigurationSerializable.class.getCanonicalName()));
    }
}
