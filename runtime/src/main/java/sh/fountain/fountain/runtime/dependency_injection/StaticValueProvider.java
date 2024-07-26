package sh.fountain.fountain.runtime.dependency_injection;

import java.util.Stack;

record StaticValueProvider<T>(T value) implements InjectionValueProvider<T> {

    @Override
    public T value(Stack<InjectionToken<?>> notYetResolvedTypes) {
        return value();
    }

    @Override
    public String name() {
        return "StaticInstance:" + value.toString();
    }

}
