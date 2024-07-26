package sh.fountain.fountain.runtime.command.parsing;

import sh.fountain.fountain.api.dependency_injection.Injectable;

import java.util.List;

@Injectable
public abstract class SingleTokenParser<T> implements ArgumentParser<T> {
    protected abstract T parse(String arg) throws ParseException;

    @Override
    public final Result<T> parse(List<String> args) throws ParseException {
        if (args.size() == 0) {
            throw new ParseException(null, "missing param");
        }

        try {
            return new Result<>(parse(args.get(0)), args.size() == 0 ? List.of() : args.subList(1, args.size()));

        } catch (ParseException exception) {
            throw exception;

        } catch (Exception error) {
            throw new ParseException(args.size() > 0 ? args.get(0) : null, error);
        }
    }
}
