package sh.fountain.fountain.runtime.command.model;

import sh.fountain.fountain.api.command.Command;
import sh.fountain.fountain.api.command.CompositeCommand;
import sh.fountain.fountain.api.command.SenderBinding;
import sh.fountain.fountain.runtime.command.completion.ArgumentCompleterFactory;
import sh.fountain.fountain.runtime.command.parsing.ArgumentParserFactory;
import sh.fountain.fountain.runtime.dependency_injection.DependencyInjector;
import sh.fountain.fountain.runtime.dependency_injection.InjectionToken;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandModelFactory {

    private final ArgumentParserFactory parserFactory;
    private final ArgumentCompleterFactory completerFactory;
    private final DependencyInjector injector;

    public CommandModelFactory(DependencyInjector injector) {
        parserFactory = new ArgumentParserFactory(injector);
        completerFactory = new ArgumentCompleterFactory(injector);
        this.injector = injector;
    }

    public record BuildResult(Set<CommandModel> commands, Map<String, Exception> errors) {
    }

    public BuildResult fromReflections(Reflections reflections) {
        final var commands = new HashSet<CommandModel>();
        final var errors = new HashMap<String, Exception>();

        reflections.getTypesAnnotatedWith(CompositeCommand.class)
                .stream()
                .filter(this::isRootCompositeCommandClass)
                .forEach(t -> {
                    try {
                        commands.add(fromCompositeCommandClass(t));
                    } catch (Exception exception) {
                        errors.put(t.getCanonicalName(), exception);
                    }
                });

        reflections.getMethodsAnnotatedWith(Command.class)
                .stream()
                .filter(m -> !isCompositeCommandClass(m.getDeclaringClass()))
                .forEach(m -> {
                    try {
                        commands.add(fromHandlerMethod(m));
                    } catch (Exception exception) {
                        errors.put(m.toString(), exception);
                    }
                });

        return new BuildResult(commands, errors);
    }

    private CompositeCommandModel fromCompositeCommandClass(Class<?> type) {
        final var compositeInfo = type.getAnnotation(CompositeCommand.class);
        final Optional<String> permission = compositeInfo.permission().equals("") ? Optional.empty()
                : Optional.of(compositeInfo.permission());

        final var singleSubcommands = Arrays.stream(type.getMethods())
                .filter(m -> m.isAnnotationPresent(Command.class))
                .map(this::fromHandlerMethod);

        final var compositeSubcommands = Arrays.stream(type.getDeclaredClasses())
                .filter(t -> t.isAnnotationPresent(CompositeCommand.class))
                .map(this::fromCompositeCommandClass);

        final var allSubcommands = Stream.concat(singleSubcommands, compositeSubcommands)
                .collect(Collectors.toMap(CommandModel::name, Function.identity()));

        return new CompositeCommandModel(compositeInfo.name(), permission, allSubcommands);
    }

    private CommandModel fromHandlerMethod(Method handler) {
        Object instance = null;
        if (!Modifier.isStatic(handler.getModifiers())) {
            instance = injector.valueFor(InjectionToken.forType(handler.getDeclaringClass()));
        }

        final var parsers = parserFactory.fromMethod(handler);
        final var completers = completerFactory.fromMethod(handler);

        final var handlerInfo = handler.getAnnotation(Command.class);
        final Optional<String> permission = handlerInfo.permission().equals("")
                ? Optional.empty()
                : Optional.of(handlerInfo.permission());
        final var name = handler.getAnnotation(Command.class).name();

        Optional<Integer> senderBindingPos = Optional.empty();

        final var parameters = handler.getParameters();
        boolean playerOnly = false;
        for (int i = 0; i < parameters.length; ++i) {
            if (parameters[i].getAnnotation(SenderBinding.class) != null) {
                senderBindingPos = Optional.of(i);

                if (parameters[i].getType().equals(Player.class)) {
                    playerOnly = true;
                }
            }
        }

        return new SingleCommandModel(name, senderBindingPos, playerOnly, permission, parsers, completers, instance,
                handler, isDeprecatedCommand(handler));
    }

    private boolean isDeprecatedCommand(Class<?> type) {
        return (type.isAnnotationPresent(Deprecated.class) && isCompositeCommandClass(type))
                || Optional.ofNullable(type.getDeclaringClass()).map(this::isDeprecatedCommand).orElse(false);
    }

    private boolean isDeprecatedCommand(Method method) {
        return method.isAnnotationPresent(Deprecated.class) || isDeprecatedCommand(method.getDeclaringClass());
    }

    private boolean isCompositeCommandClass(Class<?> type) {
        return type.isAnnotationPresent(CompositeCommand.class);
    }

    private boolean isRootCompositeCommandClass(Class<?> type) {
        return isCompositeCommandClass(type)
                && !Optional.ofNullable(type.getDeclaringClass())
                        .map(this::isCompositeCommandClass)
                        .orElse(false);
    }
}
