package sh.fountain.fountain.runtime.dependency_injection;

public class FailedToInjectException extends DependencyInjectionException {

    public FailedToInjectException(String message) {
        super(message);
    }

    public FailedToInjectException(String message, Throwable reason) {
        super(message, reason);
    }

    public static FailedToInjectException missingProvider(InjectionToken<?> token) {
        return new FailedToInjectException("No provider for token %s has been registered".formatted(token.name()));
    }
}
