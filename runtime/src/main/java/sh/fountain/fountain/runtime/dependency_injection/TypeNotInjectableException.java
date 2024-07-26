package sh.fountain.fountain.runtime.dependency_injection;

import sh.fountain.fountain.api.dependency_injection.Injectable;

import java.lang.reflect.Type;

public class TypeNotInjectableException extends DependencyInjectionException {

    public TypeNotInjectableException(String message) {
        super(message);
    }

    public static TypeNotInjectableException missingAnnotation(Type type) {
        return new TypeNotInjectableException("Type %s has not been declared injectable via annotation %s".formatted(
                type.getTypeName(),
                Injectable.class.getCanonicalName()));
    }

    public static TypeNotInjectableException notPublic(Class<?> type) {
        return new TypeNotInjectableException(
                "Type %s must be public for dependency injection".formatted(type.getCanonicalName()));
    }

    public static TypeNotInjectableException onePublicConstructor(Class<?> type) {
        return new TypeNotInjectableException(
                "Concrete type %s must have exactly one public constructor".formatted(type.getCanonicalName()));
    }

    public static TypeNotInjectableException typeUnsupported(Type type) {
        return new TypeNotInjectableException("Type %s is not supported".formatted(type.getTypeName()));
    }

    public static TypeNotInjectableException rawGenericType(Class<?> type) {
        return new TypeNotInjectableException(
                "Generic type %s may not be used raw. Type arguments missing".formatted(type.getTypeName()));
    }

    public static TypeNotInjectableException abstractTypeAsProvider(Class<?> type) {
        return new TypeNotInjectableException(
                "Abstract type %s can not be used as a provider".formatted(type.getCanonicalName()));

    }

}
