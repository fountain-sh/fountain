package sh.fountain.fountain.runtime.command.registration;

import sh.fountain.fountain.api.command.UnhandledExceptionStrategy;
import sh.fountain.fountain.runtime.command.model.CommandModel;

public interface RegistrationService {
    void register(CommandModel command, UnhandledExceptionStrategy exceptionStrategy) throws RegistrationException;
}
