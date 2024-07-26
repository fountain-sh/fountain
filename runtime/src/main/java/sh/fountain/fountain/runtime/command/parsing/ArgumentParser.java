package sh.fountain.fountain.runtime.command.parsing;

import sh.fountain.fountain.api.dependency_injection.Injectable;

import java.util.List;

@FunctionalInterface
@Injectable
public interface ArgumentParser<T> {
    record Result<T>(T parsedValue, List<String> remainingArgs) {

    }

    Result<T> parse(List<String> arguments) throws ParseException;
}
