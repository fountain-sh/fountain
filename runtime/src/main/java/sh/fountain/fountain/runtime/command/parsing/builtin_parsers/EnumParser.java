package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.runtime.command.parsing.ParseException;
import sh.fountain.fountain.runtime.command.parsing.SingleTokenParser;

import java.util.Arrays;

public class EnumParser<E extends Enum<E>> extends SingleTokenParser<E> {
    private final Class<E> enumType;

    public EnumParser(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    protected E parse(String arg) throws ParseException {
        return Arrays.stream(enumType.getEnumConstants())
                .filter(o -> o.name().equalsIgnoreCase(arg))
                .findFirst()
                .orElseThrow(() -> new ParseException(arg, "Illegal option"));
    }

}
