package sh.fountain.fountain.api.config;

/**
 * An exception that is thrown when a config file could not be loaded
 */
public class ConfigurationDeserializationException extends RuntimeException {
    public ConfigurationDeserializationException(String message) {
        super(message);
    }
}
