package sh.fountain.fountain.runtime.dependency_injection;

import sh.fountain.fountain.api.dependency_injection.Injectable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class ConstructorValueBaseClassTest {

    private DependencyInjector injector;

    @Injectable
    public interface AnInterface {

    }

    @BeforeEach
    public void setup() {
        injector = new DependencyInjector();
    }

    @Test
    @DisplayName("""
            GIVEN a annotated interface
            WHEN wrapped in an injection implementation
            THEN an exception is thrown
            """)
    public void testInterfaceException() {
        assertThrowsExactly(TypeNotInjectableException.class, () -> new ConstructorValueProvider<>(AnInterface.class, injector));
    }

    @Test
    @DisplayName("""
            GIVEN a annotated class that is not public
            WHEN wrapped in an injection implementation
            THEN an exception is thrown
            """)
    public void testNonPublicException() {

        @Injectable
        class NotPublic {

        }

        assertThrowsExactly(TypeNotInjectableException.class, () -> new ConstructorValueProvider<>(NotPublic.class, injector));
    }

    @Injectable
    public class ToManyConstructors {
        public ToManyConstructors() {

        }

        public ToManyConstructors(int i) {

        }
    }

    @Test
    @DisplayName("""
            GIVEN a annotated public class with multiple public constructors
            WHEN wrapped in an injection implementation
            THEN an exception is thrown
            """)
    public void testConstructorException() {
        assertThrowsExactly(TypeNotInjectableException.class,
                () -> new ConstructorValueProvider<>(ToManyConstructors.class, injector));
    }

    @Injectable
    public static class OnePublicConstructor {
        public OnePublicConstructor() {

        }

        private OnePublicConstructor(int i) {

        }
    }

    @Test
    @DisplayName("""
            GIVEN a annotated public class with exactly one public constructor
            WHEN wrapped in an injection implementation AND the wrappers constructor getter is queried
            THEN the public constructor is returned
            """)
    public void testConstructorGetter() throws NoSuchMethodException {
        final var impl = new ConstructorValueProvider<>(OnePublicConstructor.class, injector);

        assertEquals(OnePublicConstructor.class.getConstructor(), impl.constructor());
    }

    public static class NoAnnotation {

    }

    @Test
    @DisplayName("""
            GIVEN a class without the injectable annotation
            WHEN wrapped in an injection implemention
            THEN a exception is thrown
            """)
    public void testAnnotationException() {
        assertThrowsExactly(TypeNotInjectableException.class, () -> new ConstructorValueProvider<>(NoAnnotation.class, injector));
    }

    @Injectable
    public static class ClassA {
    }

    @Injectable
    public static class ClassB {
    }

    @Test
    public void testEquality() {
        assertEquals(new ConstructorValueProvider<>(ClassA.class, injector),
                new ConstructorValueProvider<>(ClassA.class, injector));

        assertNotEquals(new ConstructorValueProvider<>(ClassA.class, injector), new ConstructorValueProvider<>(ClassB.class, injector));

        assertNotEquals(new ConstructorValueProvider<>(ClassA.class, injector), "");

        assertEquals(new ConstructorValueProvider<>(ClassA.class, injector).hashCode(),
                new ConstructorValueProvider<>(ClassA.class, injector).hashCode());

        assertNotEquals(new ConstructorValueProvider<>(ClassA.class, injector).hashCode(),
                new ConstructorValueProvider<>(ClassB.class, injector).hashCode());

    }

}
