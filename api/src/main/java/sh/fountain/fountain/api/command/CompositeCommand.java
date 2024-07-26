package sh.fountain.fountain.api.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a parent command for all class methods marked with {@link Command}
 * and all nested classes marked with {@link CompositeCommand}
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CompositeCommand {
    String name();

    String permission() default "";
}
