package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.runtime.command.parsing.ArgumentParser;
import sh.fountain.fountain.runtime.command.parsing.ParseException;

import java.util.List;
import java.util.Optional;

public class OptionalParser<U> implements ArgumentParser<Optional<U>> {
    private final ArgumentParser<U> itemParser;

    public OptionalParser(ArgumentParser<U> itemParser) {
        this.itemParser = itemParser;
    }

    @Override
    public Result<Optional<U>> parse(List<String> args) throws ParseException {
        if (args.size() == 0) {
            return new ArgumentParser.Result<>(Optional.empty(), args);
        } else {
            try {
                final var result = itemParser.parse(args);
                return new ArgumentParser.Result<>(Optional.of(result.parsedValue()), result.remainingArgs());
            } catch (ParseException exception) {
                return new ArgumentParser.Result<>(Optional.empty(), args);
            }
        }
    }

}
