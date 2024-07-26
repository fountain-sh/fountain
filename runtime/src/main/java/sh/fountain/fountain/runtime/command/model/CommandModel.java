package sh.fountain.fountain.runtime.command.model;

import sh.fountain.fountain.runtime.text.LevenshteinDistance;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public sealed interface CommandModel permits CompositeCommandModel, SingleCommandModel {
    void run(CommandSender sender, List<String> rawArgs) throws Exception;

    List<String> tabCompletionFor(CommandSender sender, List<String> args);

    default List<String> fuzzyTabCompletionFor(CommandSender sender, List<String> args) {
        final Optional<String> lastArg = args.isEmpty() ? Optional.empty()
                : Optional.of(args.get(args.size() - 1));

        return lastArg.map(
                arg -> tabCompletionFor(sender, args)
                        .stream()
                        .filter(s -> s.toLowerCase().contains(arg.toLowerCase())
                                || LevenshteinDistance.compare(arg, s) < 4)
                        .toList())
                .orElse(List.of());
    }

    String name();

    Optional<String> permission();

    <T> T accept(CommandModelVisitor<T> visitor);
}
