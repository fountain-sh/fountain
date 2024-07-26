package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.command.parsing.ParseException;
import sh.fountain.fountain.runtime.command.parsing.SingleTokenParser;

@Injectable
public class StringParser extends SingleTokenParser<String> {

    @Override
    protected String parse(String arg) throws ParseException {
        if (arg == null) {
            throw new ParseException(arg, "may not be null");
        }
        return arg;
    }

}
