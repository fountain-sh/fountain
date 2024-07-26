package sh.fountain.fountain.runtime.dependency_injection;

import java.util.Stack;
import java.util.stream.Collectors;

public class DependencyCycleException extends DependencyInjectionException {

    public DependencyCycleException(final Stack<InjectionToken<?>> notYetResolvedTypes) {
        super("Dependency-cycle detected, type %s (in)directly depends on itself. Dependency graph: %s".formatted(
                notYetResolvedTypes.peek().name(),
                notYetResolvedTypes.stream()
                        .map(InjectionToken::name)
                        .collect(Collectors.joining(" -> "))));
    }
}
