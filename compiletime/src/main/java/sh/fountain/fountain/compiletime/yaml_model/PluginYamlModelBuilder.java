package sh.fountain.fountain.compiletime.yaml_model;

import sh.fountain.fountain.api.command.Command;
import sh.fountain.fountain.api.command.CompositeCommand;
import sh.fountain.fountain.api.plugin.Author;
import sh.fountain.fountain.api.plugin.Dependency;
import sh.fountain.fountain.api.plugin.Permission;
import sh.fountain.fountain.api.plugin.Plugin;
import sh.fountain.fountain.compiletime.yaml_model.PluginYamlModel.CommandYamlModel;
import sh.fountain.fountain.compiletime.yaml_model.PluginYamlModel.PermissionYamlModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PluginYamlModelBuilder {
    private Optional<String> main = Optional.empty();
    private Optional<String> name = Optional.empty();
    private Optional<String> version = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<String> apiVersion = Optional.empty();
    private Optional<String> load = Optional.empty();
    private List<String> authors = new ArrayList<>();
    private Optional<String> website = Optional.empty();
    private List<String> depend = new ArrayList<>();
    private Optional<String> prefix = Optional.empty();
    private List<String> softdepend = new ArrayList<>();
    private List<String> loadbefore = new ArrayList<>();
    private Map<String, CommandYamlModel> commands = new HashMap<>();
    private Map<String, PermissionYamlModel> permissions = new HashMap<>();

    public PluginYamlModel build() {
        return new PluginYamlModel(
                main.orElseThrow(() -> new IllegalArgumentException("No class has been declared a plugin entrypoint")),
                name.orElseThrow(), version.orElseThrow(), description,
                apiVersion, load, authors, website, depend, prefix, softdepend, loadbefore, commands, permissions);
    }

    public void addCommand(Command command) {
        final var permission = command.permission().isEmpty()
                ? Optional.<String>empty()
                : Optional.of(command.permission());

        commands.put(command.name(),
                new CommandYamlModel(Optional.empty(), List.of(), permission, Optional.empty(), Optional.empty()));
    }

    public void addCommand(CompositeCommand command) {
        final var permission = command.permission().isEmpty()
                ? Optional.<String>empty()
                : Optional.of(command.permission());

        commands.put(command.name(),
                new CommandYamlModel(Optional.empty(), List.of(), permission, Optional.empty(), Optional.empty()));
    }

    public void addPluginMetaData(Plugin plugin) {
        name = Optional.of(plugin.name());
        version = Optional.of(plugin.version());
        apiVersion = Optional.of(plugin.targetVersion().toString());
    }

    public void addPluginClass(String name) {
        main = Optional.of(name);
    }

    public void addPluginDependency(Dependency dependency) {
        depend.add(dependency.value());
    }

    public void addAuthor(Author author) {
        authors.add(author.value());
    }

    public void addPermission(Permission permission) {
        permissions.put(permission.value(),
                new PermissionYamlModel(Optional.empty(), Optional.empty(), Map.of()));
    }
}
