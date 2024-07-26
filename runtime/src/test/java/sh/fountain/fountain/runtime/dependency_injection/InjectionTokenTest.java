package sh.fountain.fountain.runtime.dependency_injection;

import sh.fountain.fountain.api.dependency_injection.Injectable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InjectionTokenTest {

    @Test
    @DisplayName("""
            GIVEN a type not annotated with injectable
            WHEN wrapped in an injection token
            THEN an exception is thrown""")
    public void testException() {
        assertThrowsExactly(TypeNotInjectableException.class, () -> {
            class NotAnnotated {

            }

            assertFalse(NotAnnotated.class.isAnnotationPresent(Injectable.class));

            InjectionToken.forType(NotAnnotated.class);
        });
    }

    @Test
    @DisplayName("""
            GIVEN a type annotated with injectable
            WHEN wrapped in an injection token AND queried for it's name
            THEN the wrapped types name is returned""")
    public void testName() {
        @Injectable
        interface Annotated {

        }

        assertTrue(Annotated.class.isAnnotationPresent(Injectable.class));

        final var token = InjectionToken.forType(Annotated.class);
        assertEquals("Type:" + Annotated.class.getName(), token.name());
    }
}
