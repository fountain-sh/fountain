package sh.fountain.fountain.api.config;

import sh.fountain.fountain.api.dependency_injection.Injectable;

/**
 * An object to load and parse config files
 */
@Injectable
public interface ConfigurationProvider {
    /**
     * Load the config specified by the given type
     * <p>The type used must be annotated with {@link Config}</p>
     *
     * @param <T> the type of the config model
     * @param configType the config models class object
     *
     * @return the config loaded from it's configured file
     *
     * @throws ConfigurationDeserializationException When the config model could not be deserialized
     */
    <T> T load(Class<T> configType);
}
