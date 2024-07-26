package sh.fountain.fountain.runtime.command.model;

import sh.fountain.fountain.runtime.command.parsing.ParseException;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record CompositeCommandModel(String name, Optional<String> permission, Map<String, CommandModel> subcommands)
        implements CommandModel {

    @Override
    public void run(CommandSender sender, List<String> rawArgs) throws Exception {
        if (!permission.map(sender::hasPermission).orElse(true)) {
            throw new SenderUnauthorized();
        }

        if (rawArgs.size() == 0) {
            throw new ParseException("", "Invalid subcommand");
        }

        subcommandByName(rawArgs.get(0))
                .orElseThrow(() -> new ParseException(rawArgs.get(0), "Invalid subcommand"))
                .run(sender, rawArgs.subList(1, rawArgs.size()));
    }

    @Override
    public List<String> tabCompletionFor(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            return subcommands.values()
                    .stream()
                    .filter(c -> c.permission().map(sender::hasPermission).orElse(true))
                    .map(CommandModel::name)
                    .toList();
        }

        return subcommandByName(args.get(0))
                .map(c -> c.tabCompletionFor(sender, args.subList(1, args.size())))
                .orElse(List.of());
    }

    public <T> T accept(CommandModelVisitor<T> visitor) {
        return visitor.visit(this);
    }

    private Optional<CommandModel> subcommandByName(String name) {

        if (subcommands.containsKey(name)) {
            return Optional.of(subcommands.get(name));
        } else {
            return Optional.empty();
        }
    }

}
