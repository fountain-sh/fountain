package sh.fountain.fountain.runtime.command.model;

import sh.fountain.fountain.api.command.Command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

public class DescriptionVisitor implements CommandModelVisitor<List<Component>> {

    @Override
    public List<Component> visit(CompositeCommandModel command) {
        return command.subcommands().entrySet().stream()
                .flatMap(e -> e.getValue().accept(this).stream()
                        .map(d -> Component.join(
                                JoinConfiguration.separator(Component.text(" ")),
                                Component.text(command.name())
                                        .color(NamedTextColor.GRAY)
                                        .decorate(TextDecoration.BOLD),
                                Component.text(e.getKey())
                                        .color(NamedTextColor.GRAY)
                                        .decorate(TextDecoration.BOLD),
                                d)))
                .toList();

    }

    @Override
    public List<Component> visit(SingleCommandModel command) {
        return List.of(Component.text(command.handler().getAnnotation(Command.class).description()));
    }

}
