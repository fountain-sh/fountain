package sh.fountain.fountain.runtime.command.registration;

public class RegistrationException extends Exception {
    public RegistrationException() {

    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(Throwable cause) {
        super(cause);
    }
}
