package sh.fountain.fountain.runtime.command.bukkit_interop;

import sh.fountain.fountain.api.command.UnhandledExceptionStrategy;
import sh.fountain.fountain.runtime.command.model.CommandModel;
import sh.fountain.fountain.runtime.command.model.DescriptionVisitor;
import sh.fountain.fountain.runtime.command.model.SynopsisVisitor;
import sh.fountain.fountain.runtime.command.registration.RegistrationException;
import sh.fountain.fountain.runtime.command.registration.RegistrationService;
import sh.fountain.fountain.runtime.plugin.FountainPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.stream.Collectors;

public class BukkitServer implements RegistrationService {
    private final FountainPlugin plugin;

    public BukkitServer(FountainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(CommandModel command, UnhandledExceptionStrategy exceptionStrategy)
            throws RegistrationException {
        try {
            final var bukkitCommand = plugin.getCommand(command.name());

            bukkitCommand.setExecutor(new CommandAdapter(command, exceptionStrategy));

            command.permission().ifPresent(bukkitCommand::setPermission);

            final var synopsis = command.accept(new SynopsisVisitor())
                    .stream()
                    .map(s -> Component.join(JoinConfiguration.noSeparators(), SynopsisVisitor.commandSlash(), s))
                    .map(LegacyComponentSerializer.legacySection()::serialize)
                    .collect(Collectors.joining("\n"));
            bukkitCommand.setUsage(synopsis);

            final var description = command.accept(new DescriptionVisitor()).stream()
                    .map(LegacyComponentSerializer.legacySection()::serialize)
                    .collect(Collectors.joining("\n\n"));
            bukkitCommand.setDescription(description);

        } catch (Exception error) {
            throw new RegistrationException(error);
        }

    }

}
