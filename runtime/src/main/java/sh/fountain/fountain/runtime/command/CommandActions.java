package sh.fountain.fountain.runtime.command;

import sh.fountain.fountain.api.command.UnhandledExceptionStrategy;
import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.api.plugin_actions.PluginEnableAction;
import sh.fountain.fountain.runtime.command.bukkit_interop.BukkitServer;
import sh.fountain.fountain.runtime.command.model.CommandModelFactory;
import sh.fountain.fountain.runtime.command.model.DefaultUnhandledExceptionStrategy;
import sh.fountain.fountain.runtime.dependency_injection.DependencyInjector;
import sh.fountain.fountain.runtime.dependency_injection.InjectionToken;
import sh.fountain.fountain.runtime.plugin.FountainPlugin;
import sh.fountain.fountain.runtime.reflection.ReflectionsProvider;

import java.util.Optional;

@Injectable
public class CommandActions implements PluginEnableAction {
    private final DependencyInjector injector;
    private final ReflectionsProvider reflectionsProvider;
    private final FountainPlugin plugin;

    public CommandActions(DependencyInjector injector, ReflectionsProvider reflectionsProvider, FountainPlugin plugin) {
        this.injector = injector;
        this.reflectionsProvider = reflectionsProvider;
        this.plugin = plugin;
    }

    @Override
    public void onPluginEnable() {

        final var factory = new CommandModelFactory(injector);
        final var exceptionStrategy = injector
                .valueFor(new InjectionToken<Optional<UnhandledExceptionStrategy>>() {})
                .orElse(new DefaultUnhandledExceptionStrategy());
        final var registrar = new BukkitServer(plugin);

        final var buildResult = factory.fromReflections(reflectionsProvider.reflections());

        buildResult.errors().forEach((symbol, exception) -> {
            plugin.getLogger()
                    .severe("Failed to construct command from %s: %s".formatted(symbol, exception.getMessage()));
            exception.printStackTrace();
        });

        for (final var command : buildResult.commands()) {
            try {
                registrar.register(command, exceptionStrategy);

            } catch (Exception exception) {
                plugin.getLogger().severe(
                        "Failed to register command %s: %s".formatted(command.name(), exception.getMessage()));
                exception.printStackTrace();
            }

        }
    }

}
