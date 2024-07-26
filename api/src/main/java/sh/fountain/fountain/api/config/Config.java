package sh.fountain.fountain.api.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type to be a model type for configuration.
 * <p>Configtypes must adhere to following contract:</p>
 *  <ul>
 *    <li>They must be public</li>
 *    <li>They must be a record</li>
 *    <li>
 *      They may only contains other record types, types that implement {@link ConfigurationSerializable}
 *      and the yaml primitives: {@link String}, {@code int}, {@code double} and {@code bool}
 *    </li>
 *  </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {
    /**
     * The relative path from the plugin folder to read this config from
     */
    String path();
}
