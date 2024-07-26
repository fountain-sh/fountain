package sh.fountain.fountain.runtime.dependency_injection;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.TypeUtils;
import com.destroystokyo.paper.utils.PaperPluginLogger;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.TypesAnnotated;

/**
 * An object to construct objects and their dependencies dynamically
 * <p>A dependency injector is essentially a collection of mappings from {@link InjectionToken}s to {@link InjectionValueProvider}s.
 * When a value for a token is requested the injector looks to see if a provider has been registered for said token
 * and then invokes the corresponding provider.
 * </p>
 * Following invariants are important to consider when using the injector:
 * <ul>
 *  <li>A token must only have one or zero providers</li>
 *  <li>Providers are invoked every time a value is requested. Thus providers should be idempotent.</li>
 *  <li>
 *    Providers are invoked lazily, ie. only when a value for a token is requested
 *    or when the result of a provider is required for another provider (eg. class constructor arguments)
 * </li>
 * </ul>
 */
@Injectable(useConstructor = false)
public class DependencyInjector {
    private static final Set<Class<?>> IMPLICITLY_INJECTABLES = Set.of(Optional.class, Supplier.class, Logger.class,
            PaperPluginLogger.class);

    private final Map<InjectionToken<?>, InjectionValueProvider<?>> providers = new HashMap<>();

    public DependencyInjector() {
        registerValue(this);
    }

    /**
     * Returns if a type is injectable
     * <p>A type is considered to be injectable if one of the following properties is true:</p>
     * <ul>
     *  <li>It has the {@link Injectable} annotation
     *  <li>It is one of the following types: {@link Optional}, {@link Supplier}, {@link Logger} (see {@link #IMPLICITLY_INJECTABLES})
     * </ul>
     *
     * @param type the type to check
     * @return if {@code type} is injectable
     */
    public static boolean isInjectable(Type type) {
        final var clazz = TypeUtils.classOf(type);
        return clazz.isAnnotationPresent(Injectable.class) || IMPLICITLY_INJECTABLES.contains(clazz);
    }

    public void registerFromAnnotations(Reflections reflections, Logger logger) {
        final var injectables = reflections.get(TypesAnnotated.with(Injectable.class).asClass());

        for (final var injectable : injectables) {
            try {
                final var clazz = TypeUtils.classOf(injectable);
                if (!clazz.isInterface()
                        && !Modifier.isAbstract(clazz.getModifiers())
                        && clazz.getAnnotation(Injectable.class).useConstructor()) {

                    registerConstructor(injectable);
                }

            } catch (Exception exception) {
                logger.severe("Failed to register %s as an injectable: %s".formatted(injectable.getCanonicalName(),
                        exception.getMessage()));
                exception.printStackTrace();
            }
        }
    }

    /**
     * Register a static value for the tokens representing {@code T}s type and all of {@code T}s injectable supertypes
     *
     * @param <T> the type of value
     * @param value the value to set
     * @throws ProviderCollisionException if a provider has allready been registered
     * @throws TypeNotInjectableException if {@code T} is not injectable
     */
    public <T> void registerValue(T value) {
        final var provider = new StaticValueProvider<>(value);
        registerProvider(InjectionToken.forType(value.getClass()), provider);
        allInjectableSuperTypes(value.getClass()).stream()
                .map(InjectionToken::forType)
                .forEach(token -> registerProvider(token, provider));
    }

    /**
     * Register a constructor to be invoked for the tokens {@code T}s type and all of {@code T}s injectable supertypes
     *
     * @param <T> the type of value
     * @param type the concrete type whose constructor should be used
     * @throws ProviderCollisionException if a provider has allready been registered
     * @throws TypeNotInjectableException if {@code T} is not injectable
     */
    public <T> void registerConstructor(Class<T> type) {
        final var provider = new ConstructorValueProvider<>(type, this);
        registerProvider(InjectionToken.forType(type), provider);
        allInjectableSuperTypes(type).stream()
                .map(InjectionToken::forType)
                .forEach(token -> registerProvider(token, provider));
    }

    private Set<Type> allInjectableSuperTypes(Type type) {
        return com.google.common.reflect.TypeToken.of(type).getTypes().stream()
                .map(com.google.common.reflect.TypeToken::getType)
                .filter(DependencyInjector::isInjectable)
                .collect(Collectors.toSet());
    }

    private void registerProvider(InjectionToken<?> token, InjectionValueProvider<?> provider) {
        final var registeredProviders = Optional.ofNullable(providers.get(token));
        if (!registeredProviders.map(provider::equals).orElse(true)) {
            throw new ProviderCollisionException(token, registeredProviders.get(), provider);
        }

        providers.put(token, provider);
    }

    /**
     * Returns the value for a given {@code InjectionToken}
     * <p>If the Type represented by {@code token} is {@link Optional} with type argument {@code U}, and a provider for {@code U}
     * has been registered then {@code Optional.of(valueFor(new TypeToken<U>(){}))} is returned,
     * otherwise {@code Optional.empty()} is returned.</p>
     *
     * @param <T> the type of value
     * @param token the token to retrieve a value for
     * @return the value registered for {@code token}
     */
    public <T> T valueFor(InjectionToken<T> token) {
        final var notYetResolvedTypes = new Stack<InjectionToken<?>>();
        return valueFor(token, notYetResolvedTypes);
    }

    @SuppressWarnings("unchecked")
    <T> T valueFor(InjectionToken<T> token, Stack<InjectionToken<?>> notYetResolvedTypes) {
        final var provider = providers.get(token);

        if (token.impl().getRawType().equals(Optional.class)) {
            final var underlyingToken = InjectionToken
                    .forType(((ParameterizedType) token.impl().getType()).getActualTypeArguments()[0]);

            return providers.containsKey(underlyingToken)
                    ? (T) Optional.of(valueFor(underlyingToken, notYetResolvedTypes))
                    : (T) Optional.empty();
        }

        if (provider == null) {
            throw FailedToInjectException.missingProvider(token);
        }

        return (T) provider.value(notYetResolvedTypes);
    }
}
