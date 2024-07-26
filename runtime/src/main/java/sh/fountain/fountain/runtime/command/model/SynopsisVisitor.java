package sh.fountain.fountain.runtime.command.model;

import sh.fountain.fountain.api.command.SenderBinding;
import sh.fountain.fountain.api.command.SynopsisName;
import sh.fountain.fountain.runtime.TypeUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SynopsisVisitor implements CommandModelVisitor<List<Component>> {
    public static Component commandSlash() {
        return mustKeep(Component.text("/")).color(NamedTextColor.GRAY);
    }

    public List<Component> visit(SingleCommandModel command) {
        try {
            final var handler = command.handler();

            final var nameSynopsis = mustKeep(Component.text(command.name()).color(NamedTextColor.GRAY));

            final var paramSynopsis = Arrays.stream(handler.getParameters())
                    .filter(p -> !p.isAnnotationPresent(SenderBinding.class))
                    .map(this::deriveSynopsis)
                    .toList();

            final var synopsis = new ArrayList<Component>();
            synopsis.add(nameSynopsis);
            synopsis.addAll(paramSynopsis);

            return List.of(Component.join(
                    JoinConfiguration.separator(Component.text(" ")),
                    synopsis.toArray(Component[]::new)));

        } catch (Throwable error) {
            throw new RuntimeException("Failed to generate synopsis for " + command.handler().toString(), error);
        }

    }

    public List<Component> visit(CompositeCommandModel command) {

        final var prefix = mustKeep(Component.text(command.name()).color(NamedTextColor.GRAY));

        return command.subcommands()
                .values()
                .stream()
                .flatMap(sc -> sc.accept(this).stream())
                .map(synopsisPart -> Component.join(
                        JoinConfiguration.separator(Component.text(" ")),
                        prefix,
                        synopsisPart))
                .toList();
    }

    private static Component mustKeep(Component text) {
        return text.decorate(TextDecoration.BOLD);
    }

    private static Component mustReplace(Component text) {
        return text.decorate(TextDecoration.UNDERLINED);
    }

    private static Component repeatable(Component text) {
        return Component.join(
                JoinConfiguration.noSeparators(),
                optional(text),
                Component.text("...").color(NamedTextColor.DARK_GRAY));
    }

    private static Component optional(Component text) {
        return Component.join(
                JoinConfiguration.noSeparators(),
                Component.text("[").color(NamedTextColor.DARK_GRAY),
                text,
                Component.text("]").color(NamedTextColor.DARK_GRAY));
    }

    private static Component exclusiveOptions(List<Component> options) {
        final var text = Component.text();

        options.subList(0, options.size() - 1)
                .forEach(o -> text.append(o).append(Component.text(" | ").color(NamedTextColor.DARK_GRAY)));

        return text.append(options.get(options.size() - 1)).build();
    }

    private Component deriveSynopsis(Parameter param) {
        final var synopsisInfo = Optional.ofNullable(param.getAnnotation(SynopsisName.class));

        if (param.getType().equals(Optional.class)) {
            return optional(deriveSynopsis(TypeUtils.firstGenericTypeArg(param.getParameterizedType()), synopsisInfo));

        } else if (param.getType().equals(List.class)) {
            return repeatable(deriveSynopsis(
                    TypeUtils.firstGenericTypeArg(param.getParameterizedType()), synopsisInfo));

        } else {
            return deriveSynopsis(param.getType(), synopsisInfo);
        }
    }

    private Component deriveSynopsis(Class<?> innerType, Optional<SynopsisName> synopsisInfo) {
        if (innerType.isEnum()) {
            final var enumOptions = Stream.of((Enum<?>[]) innerType.getEnumConstants())
                    .map(o -> o.name().toLowerCase())
                    .map(o -> mustKeep(Component.text(o).color(NamedTextColor.DARK_AQUA)))
                    .toList();

            return exclusiveOptions(enumOptions);

        } else {
            final var paramName = synopsisInfo
                    .map(SynopsisName::value)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "synopsis annotation required for type " + innerType.getCanonicalName()));

            if (paramName.contains(" ")) {
                throw new IllegalArgumentException("Synopsis name may not contain whitespace: " + paramName);
            }

            return mustReplace(Component.text(paramName).color(NamedTextColor.DARK_AQUA));
        }
    }
}
