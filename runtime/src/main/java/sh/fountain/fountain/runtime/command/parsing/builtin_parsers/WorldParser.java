package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.command.parsing.ParseException;
import sh.fountain.fountain.runtime.command.parsing.SingleTokenParser;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;

@Injectable
public class WorldParser extends SingleTokenParser<World> {

    @Override
    protected World parse(String arg) throws ParseException {
        return Optional
                .ofNullable(Bukkit.getWorld(arg))
                .orElseThrow(() -> new ParseException(arg, "World does not exist"));
    }

}
