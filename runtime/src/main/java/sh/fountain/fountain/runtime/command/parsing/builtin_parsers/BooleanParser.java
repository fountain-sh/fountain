package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.command.parsing.ParseException;
import sh.fountain.fountain.runtime.command.parsing.SingleTokenParser;

@Injectable
public class BooleanParser extends SingleTokenParser<Boolean> {

    @Override
    protected Boolean parse(String arg) throws ParseException {
        return switch (arg.toLowerCase()) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new ParseException(arg, "Illegal boolean value expected true or false");
        };
    }

}
