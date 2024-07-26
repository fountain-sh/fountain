package sh.fountain.fountain.runtime.command.bukkit_interop;

import sh.fountain.fountain.api.command.UnhandledExceptionStrategy;
import sh.fountain.fountain.runtime.chat_component.ErrorComponent;
import sh.fountain.fountain.runtime.command.model.CommandModel;
import sh.fountain.fountain.runtime.command.model.SenderUnauthorized;
import sh.fountain.fountain.runtime.command.parsing.NoSuchPlayerException;
import sh.fountain.fountain.runtime.command.parsing.ParseException;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandAdapter implements CommandExecutor, TabCompleter {
    private static final Component SYNTAX_ERROR_MESSAGE = ErrorComponent.text("Command invalid! Usage:")
            .color(NamedTextColor.RED);
    private static final Component AUTHORIZATION_ERROR_MESSAGE = ErrorComponent
            .text("You are not authorized to use this command!").color(NamedTextColor.RED);

    private final CommandModel command;
    private final UnhandledExceptionStrategy exceptionStrategy;

    public CommandAdapter(CommandModel command, UnhandledExceptionStrategy exceptionStrategy) {

        this.command = command;
        this.exceptionStrategy = exceptionStrategy;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command bukkitCommand,
            @NotNull String commandLabel, String[] args) {
        final var argList = List.of(args);
        try {
            command.run(sender, argList);

        } catch (SenderUnauthorized exception) {
            sender.sendMessage(AUTHORIZATION_ERROR_MESSAGE);

        } catch (NoSuchPlayerException exception) {
            final var errorMessage = ErrorComponent.text("Player ")
                    .append(Component.text(exception.badArg).color(NamedTextColor.DARK_AQUA))
                    .append(ErrorComponent.text(" could not be found!"));

            sender.sendMessage(errorMessage);

        } catch (ParseException exception) {
            sender.sendMessage(SYNTAX_ERROR_MESSAGE);
            sender.sendMessage(bukkitCommand.getUsage());

        } catch (Throwable throwable) {
            exceptionStrategy.handleException(sender, command.name(), argList, throwable);

        }

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command bukkitCommand,
            @NotNull String alias, String[] args) {
        return command.fuzzyTabCompletionFor(sender, List.of(args));
    }
}
