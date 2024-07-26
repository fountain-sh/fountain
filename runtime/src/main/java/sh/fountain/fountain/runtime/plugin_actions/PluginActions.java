package sh.fountain.fountain.runtime.plugin_actions;

import sh.fountain.fountain.api.plugin_actions.PluginDisableAction;
import sh.fountain.fountain.api.plugin_actions.PluginEnableAction;
import sh.fountain.fountain.api.plugin_actions.PluginLoadAction;
import sh.fountain.fountain.runtime.dependency_injection.DependencyInjector;
import sh.fountain.fountain.runtime.dependency_injection.InjectionToken;
import org.reflections.Reflections;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

public class PluginActions {
    @FunctionalInterface
    private interface Action {
        void run() throws Exception;
    }

    private final Set<Class<? extends PluginLoadAction>> loadActions;
    private final Set<Class<? extends PluginEnableAction>> enableActions;
    private final Set<Class<? extends PluginDisableAction>> disableActions;
    private final DependencyInjector injector;
    private final Logger logger;

    public PluginActions(DependencyInjector injector, Logger logger) {
        this.loadActions = new HashSet<>();
        this.enableActions = new HashSet<>();
        this.disableActions = new HashSet<>();
        this.injector = injector;
        this.logger = logger;
    }

    public void registerActions(Reflections reflections) {
        loadActions.addAll(reflections.getSubTypesOf(PluginLoadAction.class));
        enableActions.addAll(reflections.getSubTypesOf(PluginEnableAction.class));
        disableActions.addAll(reflections.getSubTypesOf(PluginDisableAction.class));
    }

    public void onLoad() {
        runActionPhase("load", loadActions, a -> a::onPluginLoad);
    }

    public void onEnable() {
        runActionPhase("enable", enableActions, a -> a::onPluginEnable);
    }

    public void onDisable() {
        runActionPhase("disable", disableActions, a -> a::onPluginDisable);
    }

    private <T> void runActionPhase(String phaseName, Set<Class<? extends T>> actions,
            Function<T, Action> handlerMapper) {
        logger.info("Executing %s %s actions".formatted(actions.size(), phaseName));

        var successCount = 0;
        final var allBegin = Instant.now();

        for (final var action : actions) {
            try {
                final var actionBegin = Instant.now();

                handlerMapper.apply(injector.valueFor(InjectionToken.forType(action))).run();

                final var actionEnd = Instant.now();
                ++successCount;

                final var executionTimeMs = actionBegin.until(actionEnd, ChronoUnit.MILLIS);
                if (executionTimeMs >= 500) {
                    logger.warning("%s action %s took %sms to execute"
                            .formatted(phaseName, action.getCanonicalName(), executionTimeMs));
                }

            } catch (Exception exception) {
                logger.severe("Failed to execute %s action %s: %s"
                        .formatted(phaseName, action.getCanonicalName(), exception.getMessage()));
                exception.printStackTrace();
            }
        }

        final var allEnd = Instant.now();

        logger.info("Executed %s of %s %s actions successfully in %sms"
                .formatted(successCount, actions.size(), phaseName, allBegin.until(allEnd, ChronoUnit.MILLIS)));
    }

}
