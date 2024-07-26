package sh.fountain.fountain.runtime.dependency_injection;

public class ProviderCollisionException extends DependencyInjectionException {
    public ProviderCollisionException(InjectionToken<?> token, InjectionValueProvider<?> registeredProvider,
                                      InjectionValueProvider<?> collidingProvider) {
        super("Tried to register provider %s for token %s but provider %s has already been registered"
                .formatted(collidingProvider.name(), token.name(), registeredProvider.name()));
    }
}
