package sh.fountain.fountain.api.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Represents a supplier of all possible valid values for a commands argument
 * <p>An ArgumentCompleter is assigned to a command handlers argument via the
 * {@link AutoComplete}
 * annotation. And is invoked any time tabcompletion for the corresponding argument is requested.
 * </p>
 */
@FunctionalInterface
public interface ArgumentCompleter {
    /**
     * Returns all valid values for this completers corresponding argument
     *
     * @param argsBefore the values of arguments that occur before this completers corresponding argument
     * @param sender the user tabcompletion is being performed for
     * @return All valid values for this completers corresponding argument
     */
    List<String> possibilities(List<String> argsBefore, CommandSender sender);
}
