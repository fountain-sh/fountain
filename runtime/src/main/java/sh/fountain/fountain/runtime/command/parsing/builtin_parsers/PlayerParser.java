package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.command.parsing.NoSuchPlayerException;
import sh.fountain.fountain.runtime.command.parsing.ParseException;
import sh.fountain.fountain.runtime.command.parsing.SingleTokenParser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

@Injectable
public class PlayerParser extends SingleTokenParser<Player> {

    @Override
    protected Player parse(String arg) throws ParseException {
        ;
        return Optional
                .ofNullable(Bukkit.getPlayerExact(arg))
                .orElseThrow(() -> new NoSuchPlayerException(arg));
    }

}
