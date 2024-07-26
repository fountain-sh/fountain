package sh.fountain.fountain.runtime.dependency_injection;

import sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.ClassUsingOptionalCyclicClass;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.ClassUsingStaticClass;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.MultiParamClass;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.NonCyclicClass;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.StaticClass;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.simple_cycle.FirstClass;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.trivial_cycle.CyclicClass;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_generic_injectables.GenericProviderInterface;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_injectables.BaseClass;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_injectables.ChildClass;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_injectables.Consumer;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_injectables.ManualValue;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_injectables.ManualValueInterface;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_no_injectables.SomeClass;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class DependencyInjectorTest {
    @Test
    @DisplayName("""
            GIVEN a package p
            WHEN the dependency injector registers p's types via it's reflections
            AND an instance for an injectable type T with a constructor C with Parameters of injectable types is requested
            THEN a wellformed instance of T is returned
            """)
    void testRegistrationFromReflections() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_injectables",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        assertTrue(injector
                .valueFor(InjectionToken.forType(Consumer.class)) instanceof Consumer);
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector registered from a package with no injectables
            WHEN an instance for any type is requested
            THEN an exception is thrown
            """)
    void testInstanceFromNonInjectableException() {
        final var reflections = new Reflections(
                getClass().getPackageName() + ".test_package_with_no_injectables",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        assertThrowsExactly(TypeNotInjectableException.class,
                () -> injector.valueFor(InjectionToken.forType(SomeClass.class)));
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector registered from a package with a class depending on itself
            WHEN an instance for the dependent type is requested
            THEN an exception is thrown
            """)
    void testTrivialCycle() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_cycle",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        assertNotRootCause(DependencyCycleException.class, () -> injector.valueFor(InjectionToken.forType(CyclicClass.class)));
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector registered from a package with a class graph containing a cycle
            WHEN the root of the class graph is requested
            THEN an exception is thrown
            """)
    void testNonTrivialCycle() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_cycle",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        assertNotRootCause(DependencyCycleException.class, () -> injector.valueFor(InjectionToken.forType(FirstClass.class)));
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector registered from a package with a class with multiple params where the last is cyclic
            WHEN an instance for the class is requested
            THEN an exception is thrown
            """)
    void testMultiParamClass() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_cycle",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        assertNotRootCause(DependencyCycleException.class, () -> injector.valueFor(InjectionToken.forType(MultiParamClass.class)));
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector registered from a package with a class with an optional cyclic param
            WHEN an instance for the class is requested
            THEN an exception is thrown
            """)
    void testOptionalCycle() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_cycle",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        assertNotRootCause(DependencyCycleException.class, () -> injector.valueFor(InjectionToken.forType(ClassUsingOptionalCyclicClass.class)));
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector registered from a package with a class with with a statically provided param and a cyclic param
            WHEN an instance for the class is requested
            THEN an exception is thrown
            """)
    void testCycleWithStaticValueProvider() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_cycle",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerValue(new StaticClass());
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        assertNotRootCause(DependencyCycleException.class, () -> injector.valueFor(InjectionToken.forType(ClassUsingStaticClass.class)));
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector registered from a package with no cyclic dependencies
            WHEN an instance for a class is requested
            THEN no exception is thrown
            """)
    void testNoCycle() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_cycle",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        Assertions.assertDoesNotThrow(() -> injector.valueFor(InjectionToken.forType(NonCyclicClass.class)));
    }

    private void assertNotRootCause(Class<? extends Exception> causeClass, Executable executable) {
        Assertions.assertEquals(causeClass, ExceptionUtils
                .getRootCause(Assertions.assertThrows(FailedToInjectException.class, executable)).getClass());
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector registered from a package with injectables
            WHEN an instance for an injectable type is requested multiple times
            THEN the same instance is returned
            """)
    void testOnlyOneInstancePerImplementation() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_injectables",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        IntStream.range(0, 10).boxed()
                .map(i -> injector.valueFor(InjectionToken.forType(Consumer.class)))
                .reduce((i1, i2) -> {
                    assertSame(i1, i2);
                    return i2;
                });
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector AND a injectable type T that disabled constructor usage implementing injectable interface U
            WHEN an instance for type T is registered AND an instance for T is requested AND an instance for U is requested
            THEN the same object is returned by the injector
            """)
    void testManualInstanceRegistration() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_injectables",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        final var instance = new ManualValue();
        injector.registerValue(instance);

        assertSame(instance, injector.valueFor(InjectionToken.forType(ManualValue.class)));
        assertSame(instance, injector.valueFor(InjectionToken.forType(ManualValueInterface.class)));
    }

    @Test
    @DisplayName("""
            GIVEN a dependency injector where a value for type T has been successfully registered
            WHEN an instance for T is manually registered
            THEN an exception is thrown
            """)
    void testManualInstanceRegistrationException() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_injectables",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));
        final var instance = new ManualValue();
        injector.registerValue(instance);

        assertThrowsExactly(ProviderCollisionException.class, () -> injector.registerValue(new ManualValue()));
    }

    @Test
    void testNoProvider() {
        final var reflections = new Reflections(getClass().getPackageName() + ".test_package_with_injectables",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations((reflections), mock(Logger.class));

        assertThrowsExactly(FailedToInjectException.class,
                () -> injector.valueFor(InjectionToken.forType(ManualValueInterface.class)));
    }

    @Test
    public void testGenerics() {
        assertDoesNotThrow(() -> {
            final var reflections = new Reflections(
                    getClass().getPackageName() + ".test_package_with_generic_injectables",
                    Scanners.values());

            final var injector = new DependencyInjector();
            injector.registerFromAnnotations(reflections, mock(Logger.class));

            final var instance = injector
                    .valueFor(new InjectionToken<GenericProviderInterface<Optional<String>, Integer>>() {
                    });
            assertNotNull(instance);

            assertThrowsExactly(TypeNotInjectableException.class,
                    () -> injector.valueFor(InjectionToken.forType(GenericProviderInterface.class)));
        });

    }

    @Test
    public void testOptionalInjection() {
        final var reflections = new Reflections(
                getClass().getPackageName() + ".test_package_with_generic_injectables",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        final var instance1 = injector.valueFor(
                new InjectionToken<Optional<GenericProviderInterface<Integer, Integer>>>() {
                });
        assertTrue(instance1.isEmpty());

        final var instance2 = injector
                .valueFor(new InjectionToken<Optional<GenericProviderInterface<String, Integer>>>() {

                });
        assertTrue(instance2.isPresent());
    }

    @Test
    public void testSuperClass() {
        final var reflections = new Reflections(
                getClass().getPackageName() + ".test_package_with_injectables",
                Scanners.values());

        final var injector = new DependencyInjector();
        injector.registerFromAnnotations(reflections, mock(Logger.class));

        final var value = injector.valueFor(InjectionToken.forType(BaseClass.class));

        assertTrue(value instanceof ChildClass);
    }
}
