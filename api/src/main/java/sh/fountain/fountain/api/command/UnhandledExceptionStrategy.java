package sh.fountain.fountain.api.command;

import sh.fountain.fountain.api.dependency_injection.Injectable;

import org.bukkit.command.CommandSender;

import java.util.List;

@Injectable
public interface UnhandledExceptionStrategy {
    void handleException(CommandSender sender, String commandName, List<String> args, Throwable exception);
}
