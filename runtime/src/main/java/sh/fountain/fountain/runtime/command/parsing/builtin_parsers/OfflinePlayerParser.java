package sh.fountain.fountain.runtime.command.parsing.builtin_parsers;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.command.parsing.NoSuchPlayerException;
import sh.fountain.fountain.runtime.command.parsing.ParseException;
import sh.fountain.fountain.runtime.command.parsing.SingleTokenParser;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

@Injectable
public class OfflinePlayerParser extends SingleTokenParser<OfflinePlayer> {

    @Override
    protected OfflinePlayer parse(String arg) throws ParseException {
        return Optional
                .ofNullable(Bukkit.getOfflinePlayerIfCached(arg))
                .orElseThrow(() -> new NoSuchPlayerException(arg));
    }

}
