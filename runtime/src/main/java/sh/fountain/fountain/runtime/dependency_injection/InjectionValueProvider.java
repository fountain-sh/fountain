package sh.fountain.fountain.runtime.dependency_injection;

import java.util.Stack;

/**
 * Interface of a value provider
 *
 * <p>An injection value provider encapsulates how a {@link DependencyInjector}
 * resolves a value for a given {@link InjectionToken}.</p>
 */
sealed interface InjectionValueProvider<T> permits ConstructorValueProvider, StaticValueProvider {
    T value(Stack<InjectionToken<?>> notYetResolvedTypes);

    String name();
}
