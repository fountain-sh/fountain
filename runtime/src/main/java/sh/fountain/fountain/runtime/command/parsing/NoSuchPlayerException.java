package sh.fountain.fountain.runtime.command.parsing;

public class NoSuchPlayerException extends ParseException {
    public NoSuchPlayerException(String arg) {
        super(arg, "Player could not be found");
    }
}
