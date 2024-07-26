package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.runtime.command.parsing.ArgumentParser;
import sh.fountain.fountain.runtime.command.parsing.ParseException;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class CollectionParser<U, T extends Collection<U>> implements ArgumentParser<T> {
    private final Supplier<T> collectionConstructor;
    private final ArgumentParser<U> itemParser;

    public CollectionParser(Supplier<T> collectionConstructor, ArgumentParser<U> itemParser) {
        this.collectionConstructor = collectionConstructor;
        this.itemParser = itemParser;
    }

    @Override
    public Result<T> parse(List<String> arguments) throws ParseException {
        final var value = collectionConstructor.get();

        var remainingArgs = arguments;
        while (remainingArgs.size() > 0) {
            final var result = itemParser.parse(remainingArgs);

            value.add(result.parsedValue());
            remainingArgs = result.remainingArgs();
        }

        return new Result<>(value, remainingArgs);
    }

}
