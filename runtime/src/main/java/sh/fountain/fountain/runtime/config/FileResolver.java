package sh.fountain.fountain.runtime.config;

import sh.fountain.fountain.api.config.Config;
import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.plugin.FountainPlugin;

import java.io.File;
import java.nio.file.Path;

@Injectable
public class FileResolver {
    private final FountainPlugin plugin;

    public FileResolver(FountainPlugin plugin) {
        this.plugin = plugin;
    }

    public File resolve(Config config) {
        return Path.of(plugin.getDataFolder().toPath().toString(), config.path()).toFile();
    }
}
