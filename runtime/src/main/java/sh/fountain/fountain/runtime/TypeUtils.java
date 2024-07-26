package sh.fountain.fountain.runtime;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtils {
    private TypeUtils() {
    }

    public static Class<?> classOf(Type type) {
        if (type instanceof final Class<?> c) {
            return c;
        } else if (type instanceof final ParameterizedType p) {
            return (Class<?>) p.getRawType();
        } else {
            throw new IllegalArgumentException(
                    "Type %s can not be used as an InjectionToken".formatted(type.getTypeName()));
        }
    }

    public static Class<?> firstGenericTypeArg(Type type) {
        if (type instanceof final ParameterizedType p) {
            return classOf(p.getActualTypeArguments()[0]);
        } else {
            throw new IllegalArgumentException("Type is not parameterized");
        }
    }
}
