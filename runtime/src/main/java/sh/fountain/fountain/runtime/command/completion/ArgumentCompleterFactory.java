package sh.fountain.fountain.runtime.command.completion;

import sh.fountain.fountain.api.command.ArgumentCompleter;
import sh.fountain.fountain.api.command.AutoComplete;
import sh.fountain.fountain.api.command.SenderBinding;
import sh.fountain.fountain.runtime.TypeUtils;
import sh.fountain.fountain.runtime.dependency_injection.DependencyInjector;
import sh.fountain.fountain.runtime.dependency_injection.InjectionToken;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record ArgumentCompleterFactory(DependencyInjector injector) {

    public Optional<ArgumentCompleter> fromParameter(Parameter param) {
        if (param.getAnnotation(AutoComplete.class) != null) {

            return Optional.of(injector.valueFor(InjectionToken.forType(param.getAnnotation(AutoComplete.class).value())));

        } else if (param.getType().equals(Optional.class) || param.getType().equals(List.class)) {
            return deriveCompleter(TypeUtils.firstGenericTypeArg(param.getParameterizedType()));
        } else {
            return deriveCompleter(param.getType());
        }
    }

    private Optional<ArgumentCompleter> deriveCompleter(Class<?> type) {
        if (type.equals(Player.class)) {

            return Optional.of(
                    (argsBefore, sender) -> Bukkit.getOnlinePlayers()
                            .stream()
                            .filter(p -> sender instanceof Player s ? s.canSee(p) : true)
                            .map(Player::getName)
                            .toList());

        } else if (type.equals(OfflinePlayer.class)) {
            return Optional.of(
                    (argsBefore, sender) -> Arrays.stream(Bukkit.getOfflinePlayers())
                            .map(p -> p.getName())
                            .filter(Objects::nonNull)
                            .toList());

        } else if (type.equals(World.class)) {
            return Optional.of(((argsBefore, sender) -> Bukkit.getWorlds().stream().map(World::getName).toList()));
        } else if (type.isEnum()) {
            final var options = (Enum<?>[]) type.getEnumConstants();
            return Optional.of(
                    (argsBefore, sender) -> Arrays.stream(options)
                            .map(e -> e.name().toLowerCase())
                            .toList());

        } else {
            return Optional.empty();
        }
    }

    public List<Optional<ArgumentCompleter>> fromMethod(Method method) {
        return Arrays.stream(method.getParameters())
                .filter(p -> !p.isAnnotationPresent(SenderBinding.class))
                .map(this::fromParameter)
                .toList();
    }
}
