package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.command.parsing.ParseException;
import sh.fountain.fountain.runtime.command.parsing.SingleTokenParser;

@Injectable
public class DoubleParser extends SingleTokenParser<Double> {

    @Override
    protected Double parse(String arg) throws ParseException {
        return Double.parseDouble(arg);
    }

}
