package sh.fountain.fountain.compiletime.yaml_model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@JsonInclude(Include.NON_EMPTY)
public record PluginYamlModel(String main, String name, String version, Optional<String> description,
        @JsonProperty("api-version") Optional<String> apiVersion, Optional<String> load, List<String> authors,
        Optional<String> website,
        List<String> depend, Optional<String> prefix, List<String> softdepend, List<String> loadbefore,
        @JsonInclude(Include.NON_EMPTY) Map<String, CommandYamlModel> commands,
        @JsonInclude(Include.NON_EMPTY) Map<String, PermissionYamlModel> permissions) {
    @JsonInclude(Include.NON_EMPTY)
    public record CommandYamlModel(Optional<String> description, List<String> aliases, Optional<String> permission,
            @JsonProperty("permission-message") Optional<String> permissionMessage,
            Optional<String> usage) {
    }

    @JsonInclude(Include.NON_EMPTY)
    public record PermissionYamlModel(Optional<String> description,
            @JsonProperty("default") Optional<String> defaultValue,
            Map<String, Boolean> children) {
    }
}
