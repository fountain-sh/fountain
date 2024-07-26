package sh.fountain.fountain.runtime.dependency_injection;

public abstract class DependencyInjectionException extends RuntimeException {
    protected DependencyInjectionException(String message) {
        super(message);
    }

    protected DependencyInjectionException(String message, Throwable reason) {
        super(message, reason);
    }
}
