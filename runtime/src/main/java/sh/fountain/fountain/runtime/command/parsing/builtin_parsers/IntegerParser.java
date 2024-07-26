package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.command.parsing.ParseException;
import sh.fountain.fountain.runtime.command.parsing.SingleTokenParser;

@Injectable
public class IntegerParser extends SingleTokenParser<Integer> {

    @Override
    protected Integer parse(String arg) throws ParseException {
        return Integer.parseInt(arg);
    }

}
