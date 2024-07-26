package sh.fountain.fountain.runtime.dependency_injection;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Base class of an {@link InjectionToken} representing an injectable type
 *
 * @see DependencyInjector#isInjectable(Type)
 */
public abstract class InjectionToken<T> {
    private final TypeToken<T> impl;

    @SuppressWarnings("unchecked")
    protected InjectionToken() {
        impl = (TypeToken<T>) TypeToken
                .of(((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0]);
        checkType();
    }

    InjectionToken(TypeToken<T> impl) {
        this.impl = impl;

        checkType();
    }

    /**
     * Construct a {@link InjectionToken} for a type
     * <p>If an instance of {@link ParameterizedType} for the corresponding type is not available use an anonymous class instead:</p>
     * <pre>
     * {@code TypeToken<Optional<String>> token = new TypeToken<Optional<String>>(){};}
     * </pre>
     *
     * @param <T> type the token should refer to. Must be explicitly passed
     *
     * @throws TypeNotInjectableException if the passed type has not been declared injectable
     */
    @SuppressWarnings("unchecked")
    public static <T> InjectionToken<T> forType(Type type) {
        return new InjectionToken<>((TypeToken<T>) TypeToken.of(type)) {
        };
    }

    /**
     * Construct a {@link InjectionToken} for a type
     * <p>Due to JVM limitations, instances of {@link Class} may only be passed if the corresponding type is not generic.</p>
     *
     * @param <T> type the token should refer to
     * @throws TypeNotInjectableException if the passed type is a raw generic type or the type has not been declared injectable
     */
    public static <T> InjectionToken<T> forType(Class<T> type) {
        return InjectionToken.forType((Type) type);
    }

    public static <T> InjectionToken<T> from(TypeToken<T> impl) {
        return new InjectionToken<>(impl) {
        };
    }

    private void checkType() {
        if (!DependencyInjector.isInjectable(impl.getType())) {
            throw TypeNotInjectableException.missingAnnotation(impl.getType());
        }

        if (impl.getType() instanceof Class<?> c && c.getTypeParameters().length != 0) {
            throw TypeNotInjectableException.rawGenericType(c);
        }
    }

    public final String name() {
        return "Type:" + impl.toString();
    }

    TypeToken<T> impl() {
        return impl;
    }

    @Override
    public int hashCode() {
        return impl.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof InjectionToken<?> o && impl.equals(o.impl);
    }

}
