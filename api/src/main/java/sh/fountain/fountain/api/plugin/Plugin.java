package sh.fountain.fountain.api.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The plugin's metadata
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)

public @interface Plugin {
    /**
     * The plugin name
     */
    String name();

    /**
     * The plugin's version
     */
    String version();

    /**
     * The plugin's targeted {@link ServerApiVersion}
     */
    ServerApiVersion targetVersion();
}
