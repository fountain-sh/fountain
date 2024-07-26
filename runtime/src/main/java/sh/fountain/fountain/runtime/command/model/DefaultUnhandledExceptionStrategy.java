package sh.fountain.fountain.runtime.command.model;

import sh.fountain.fountain.api.command.UnhandledExceptionStrategy;
import sh.fountain.fountain.runtime.chat_component.ErrorComponent;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DefaultUnhandledExceptionStrategy implements UnhandledExceptionStrategy {

    private static final Component INTERNAL_ERROR_MESSAGE = ErrorComponent
            .text("Ein interner Fehler bei der Ausführung des Kommandos ist aufgetreten.");

    @Override
    public void handleException(CommandSender sender, String commandName, List<String> args, Throwable exception) {
        sender.sendMessage(INTERNAL_ERROR_MESSAGE);
        exception.printStackTrace();

    }
}
