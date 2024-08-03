package sh.fountain.fountain.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Assigns an {@link ArgumentCompleter} to a command handler's argument
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoComplete {
    Class<? extends ArgumentCompleter> value();
}
