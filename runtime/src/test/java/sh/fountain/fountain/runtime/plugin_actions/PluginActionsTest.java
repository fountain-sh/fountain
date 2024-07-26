package sh.fountain.fountain.runtime.plugin_actions;

import sh.fountain.fountain.runtime.dependency_injection.DependencyInjector;
import sh.fountain.fountain.runtime.plugin_actions.test_package.DisableAction;
import sh.fountain.fountain.runtime.plugin_actions.test_package.EnableAction;
import sh.fountain.fountain.runtime.plugin_actions.test_package.LoadAction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

public class PluginActionsTest {
    @Test
    @DisplayName("""
            GIVEN a package with a load action
            WHEN the onLoad phase is executed
            THEN the action is constructed and executed one time
            """)
    void testLoadAction() {
        try (final var constructor = mockConstruction(LoadAction.class, withSettings().useConstructor())) {
            final var injector = new DependencyInjector();
            final var reflections = new Reflections(LoadAction.class.getPackageName(), Scanners.values());

            injector.registerFromAnnotations(reflections, mock(Logger.class));

            final var actions = new PluginActions(injector, mock(Logger.class));
            actions.registerActions(reflections);

            assertEquals(0, constructor.constructed().size());
            actions.onLoad();
            assertEquals(1, constructor.constructed().size());
            verify(constructor.constructed().get(0), times(1)).onPluginLoad();
        }
    }

    @Test
    @DisplayName("""
            GIVEN a package with an enable action
            WHEN the onEnable phase is executed
            THEN the action is constructed and executed one time
            """)
    void testEnableAction() {
        try (final var constructor = mockConstruction(EnableAction.class, withSettings().useConstructor())) {
            final var injector = new DependencyInjector();
            final var reflections = new Reflections(EnableAction.class.getPackageName(), Scanners.values());

            injector.registerFromAnnotations(reflections, mock(Logger.class));

            final var actions = new PluginActions(injector, mock(Logger.class));
            actions.registerActions(reflections);

            actions.onLoad();

            assertEquals(0, constructor.constructed().size());
            actions.onEnable();
            assertEquals(1, constructor.constructed().size());
            verify(constructor.constructed().get(0), times(1)).onPluginEnable();
        }
    }

    @Test
    @DisplayName("""
            GIVEN a package with a disable action
            WHEN the onDisable phase is executed
            THEN the action is constructed and executed one time
            """)
    void testDisable() {
        try (final var constructor = mockConstruction(DisableAction.class, withSettings().useConstructor())) {
            final var injector = new DependencyInjector();
            final var reflections = new Reflections(DisableAction.class.getPackageName(), Scanners.values());

            injector.registerFromAnnotations(reflections, mock(Logger.class));

            final var actions = new PluginActions(injector, mock(Logger.class));
            actions.registerActions(reflections);

            actions.onLoad();
            actions.onEnable();

            assertEquals(0, constructor.constructed().size());
            actions.onDisable();
            assertEquals(1, constructor.constructed().size());
            verify(constructor.constructed().get(0), times(1)).onPluginDisable();
        }
    }
}
