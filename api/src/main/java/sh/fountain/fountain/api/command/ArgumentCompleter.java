package sh.fountain.fountain.api.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Represents a supplier of all possible valid values for a command's argument
 * <p>An ArgumentCompleter is assigned to a command handler's argument via the
 * {@link AutoComplete}
 * annotation. And is invoked any time tab-completion for the corresponding argument is requested.
 * </p>
 */
@FunctionalInterface
public interface ArgumentCompleter {
    /**
     * Returns all valid values for this completer's corresponding argument
     *
     * @param argsBefore the values of arguments that occur before this completer's corresponding argument
     * @param sender the user tab-completion is being performed for
     * @return All valid values for this completer's corresponding argument
     */
    List<String> possibilities(List<String> argsBefore, CommandSender sender);
}
