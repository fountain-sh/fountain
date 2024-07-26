package sh.fountain.fountain.runtime.listener;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.api.plugin_actions.PluginEnableAction;
import sh.fountain.fountain.runtime.dependency_injection.DependencyInjector;
import sh.fountain.fountain.runtime.plugin.FountainPlugin;
import sh.fountain.fountain.runtime.reflection.ReflectionsProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.stream.Collectors;

@Injectable
public class ListenerActions implements PluginEnableAction {
    private final ReflectionsProvider reflectionsProvider;
    private final FountainPlugin plugin;

    public ListenerActions(ReflectionsProvider reflectionsProvider, FountainPlugin plugin) {
        this.reflectionsProvider = reflectionsProvider;
        this.plugin = plugin;
    }

    @Override
    public void onPluginEnable() {
        final var listenerTypes = reflectionsProvider.reflections().getSubTypesOf(Listener.class)
                .stream()
                .collect(Collectors.groupingBy(DependencyInjector::isInjectable));

        for (final var listener : listenerTypes.get(true)) {
            try {
                Bukkit.getPluginManager().registerEvents(plugin.inject(listener), plugin);
            } catch (Exception exception) {
                plugin.getLogger().severe("Failed to register listener %s: %s".formatted(listener.getCanonicalName(),
                        exception.getMessage()));
                exception.printStackTrace();
            }
        }

        listenerTypes.get(false).forEach(l -> plugin.getLogger().warning(
                "Listener " + l.getCanonicalName() + " has not been automatically registered as it is not injectable"));
    }

}
