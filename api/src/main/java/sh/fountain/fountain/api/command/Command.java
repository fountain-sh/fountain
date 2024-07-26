package sh.fountain.fountain.api.command;

import org.bukkit.command.CommandSender;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * Marks a method as a command handler
 * <p>The command handler is invoked every time a command is executed.
 * Any argument which is not of type {@link String} is initialized with the result of it's types
 * ArgumentParser. One Argument of type {@link CommandSender} may be annotated with {@link SenderBinding}
 * to initialize is with the commands sender. Arguments of type {@link Optional} denote optional arguments.
 * Arguments of type {@link List}, {@link Set}, {@link Queue} or {@link Deque} denote an argument may be passed multiple times (or not at all).
 * These arguments will then be passed in order of supply to the underlying Collection and handled as specified by the Collection.
 * </p>
 *
 * <p>Non enum arguments must be annotated with {@link SynopsisName}!</p>
 *
 * <p>If the methods {@code m} declaring class {@code C} is marked with {@link CompositeCommand}
 * {@code m}s command will be added as a subcommand of {@code C}s command</p>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * The in-game name of the command
     */
    String name();

    /**
     * The permission required by any user to execute the command
     */
    String permission() default "";

    /**
     * A short summary of what the command does
     */
    String description();
}
