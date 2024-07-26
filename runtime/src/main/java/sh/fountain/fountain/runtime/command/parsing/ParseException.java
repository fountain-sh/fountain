package sh.fountain.fountain.runtime.command.parsing;

public class ParseException extends Exception {

    public final String badArg;

    public ParseException(String badArg, String message) {
        super(message);

        this.badArg = badArg;
    }

    public ParseException(String badArg, Throwable cause) {
        super(cause);

        this.badArg = badArg;
    }
}
