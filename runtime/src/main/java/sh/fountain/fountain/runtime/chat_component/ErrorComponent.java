package sh.fountain.fountain.runtime.chat_component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ErrorComponent {
    private ErrorComponent() {
    }

    public static Component text(String message) {
        return Component.text(message).color(NamedTextColor.RED);
    }

}
