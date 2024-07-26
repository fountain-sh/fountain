package sh.fountain.fountain.runtime.dependency_injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

/**
 * Represents an {@link InjectionValueProvider} that produces a value by invoking a concrete classes constructor
 */
final class ConstructorValueProvider<T> implements InjectionValueProvider<T> {
    private final Class<T> type;
    private final DependencyInjector injector;
    private Optional<T> valueCache = Optional.empty();

    public ConstructorValueProvider(Class<T> type, DependencyInjector injector) {
        this.type = type;
        this.injector = injector;
        if (!Modifier.isPublic(type.getModifiers())) {
            throw TypeNotInjectableException.notPublic(type);
        }

        if (!DependencyInjector.isInjectable(type)) {
            throw TypeNotInjectableException.missingAnnotation(type);
        }

        if (Modifier.isAbstract(type.getModifiers())) {
            throw TypeNotInjectableException.abstractTypeAsProvider(type);
        }

        if (type.getConstructors().length != 1) {
            throw TypeNotInjectableException.onePublicConstructor(type);
        }
    }

    public String name() {
        return "Constructor:" + constructor().getName();
    }

    Constructor<?> constructor() {
        return type.getConstructors()[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public T value(Stack<InjectionToken<?>> notYetResolvedTypes) {
        final var value = valueCache.orElseGet(() -> {
            final var args = new ArrayList<>();
            notYetResolvedTypes.push(InjectionToken.forType(type));

            try {
                for (final var parameter : constructor().getParameters()) {
                    final var token = InjectionToken.forType(parameter.getParameterizedType());
                    if (notYetResolvedTypes.contains(token)) {
                        notYetResolvedTypes.push(token);
                        throw new DependencyCycleException(notYetResolvedTypes);
                    }
                    args.add(injector.valueFor(token, notYetResolvedTypes));
                }

                notYetResolvedTypes.pop();
                return (T) constructor().newInstance(args.toArray());

            } catch (Exception exception) {
                throw new FailedToInjectException(
                        "Failed to construct instance of %s".formatted(type.getCanonicalName()),
                        exception);
            }
        });

        valueCache = Optional.of(value);

        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ConstructorValueProvider<?> c && c.type.equals(type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
