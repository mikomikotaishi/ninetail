package bot.ninetail.utilities;

import jakarta.annotation.Nonnull;

import lombok.experimental.UtilityClass;

/**
 * Utility class for text formatting operations.
 */
@UtilityClass
public final class TextFormat {
    public String verbatim(@Nonnull String s) {
        return String.format("`%s`", s);
    }
}
