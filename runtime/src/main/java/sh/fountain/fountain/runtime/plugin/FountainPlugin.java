package sh.fountain.fountain.runtime.plugin;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.dependency_injection.DependencyInjector;
import sh.fountain.fountain.runtime.dependency_injection.InjectionToken;
import sh.fountain.fountain.runtime.plugin_actions.PluginActions;
import sh.fountain.fountain.runtime.reflection.ReflectionsProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

@Injectable()
public abstract class FountainPlugin extends JavaPlugin {
    private final DependencyInjector injector = new DependencyInjector();
    private final PluginActions actions = new PluginActions(injector, getLogger());

    @Override
    public final void onLoad() {
        injector.registerValue(this);
        injector.registerValue(getLogger());
        final var reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(getClass().getPackageName(), "sh.fountain.fountain")
                        .setScanners(Scanners.values()));

        injector.registerFromAnnotations(reflections, getLogger());
        injector.registerValue(new ReflectionsProvider(reflections));

        actions.registerActions(reflections);

        actions.onLoad();

        getLogger().info("Loaded %s successfully".formatted(getName()));
    }

    @Override
    public final void onEnable() {
        actions.onEnable();
        getLogger().info("Enabled %s successfully".formatted(getName()));
    }

    @Override
    public final void onDisable() {
        actions.onDisable();
        getLogger().info("Disabled %s successfully".formatted(getName()));
    }

    /**
     * Returns the instance for a given {@link Class} of type {@code T}
     *
     * @param <T> the type of value
     * @param clazz the clazz to retrieve an instance for
     * @return the value registered for type {@code T}
     * @throws sh.fountain.fountain.runtime.dependency_injection.DependencyInjectionException if injection fails
     */
    public <T> T inject(Class<T> clazz) {
        return injector.valueFor(InjectionToken.forType(clazz));
    }
}
