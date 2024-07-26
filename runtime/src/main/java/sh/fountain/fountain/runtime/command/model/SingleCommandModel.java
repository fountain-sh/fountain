package sh.fountain.fountain.runtime.command.model;

import sh.fountain.fountain.api.command.ArgumentCompleter;
import sh.fountain.fountain.runtime.chat_component.ErrorComponent;
import sh.fountain.fountain.runtime.command.parsing.ArgumentParser;
import sh.fountain.fountain.runtime.command.parsing.ParseException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public record SingleCommandModel(
        String name,
        Optional<Integer> senderBindingPos,
        boolean playerOnly,
        Optional<String> permission,
        List<? extends ArgumentParser<?>> parsers,
        List<Optional<ArgumentCompleter>> completers,
        Object instance,
        Method handler,
        boolean isDeprecated) implements CommandModel {

    public SingleCommandModel {
        assert parsers.size() == completers.size();
        assert handler.getParameterCount() - senderBindingPos.map(i -> 1).orElse(0) == parsers
                .size();
    }

    @Override
    public void run(CommandSender sender, List<String> rawArgs) throws Exception {
        if (!permission.map(sender::hasPermission).orElse(true)) {
            throw new SenderUnauthorized();
        }

        if (playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(ErrorComponent.text("Only players can run this command"));
            return;
        }

        if (isDeprecated) {
            sender.sendMessage(
                    ErrorComponent.text("This command has been deprecated and should not be used anymore"));
        }

        final var parsedArgs = new ArrayList<>();
        var remainingArgs = rawArgs;

        for (final var parser : parsers) {

            final var result = parser.parse(remainingArgs);

            parsedArgs.add(result.parsedValue());
            remainingArgs = result.remainingArgs();
        }

        if (remainingArgs.size() > 0) {
            throw new ParseException(remainingArgs.get(remainingArgs.size() - 1), "Unused arg");
        }

        final var args = senderBindingPos.map(pos -> {
            final var before = parsedArgs.subList(0, pos);
            final var after = parsedArgs.subList(pos, parsedArgs.size());

            final var allArgs = new ArrayList<>(before);
            allArgs.add(sender);
            allArgs.addAll(after);

            return allArgs;
        }).orElse(parsedArgs).toArray();

        handler.invoke(instance, args);
    }

    public <T> T accept(CommandModelVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<String> tabCompletionFor(CommandSender sender, List<String> args) {
        if (args.size() > completers.size()) {
            if (handler.getParameterCount() > 0
                    && Collection.class.isAssignableFrom(handler.getParameterTypes()[handler.getParameterCount() - 1])) {
                return completers.get(completers.size() - 1).map(c -> c.possibilities(cutLastArg(args), sender))
                        .orElse(List.of());

            } else {
                return List.of();
            }

        } else {
            return completers.get(args.size() - 1).map(c -> c.possibilities(cutLastArg(args), sender))
                    .orElse(List.of());

        }
    }

    private List<String> cutLastArg(List<String> args) {
        if (args.size() == 0) {
            return new LinkedList<>();
        } else if (args.size() == 1) {
            return List.of(args.get(0));
        } else {
            return args.subList(0, args.size() - 1);
        }
    }

}
