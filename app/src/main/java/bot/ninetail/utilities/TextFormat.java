package bot.ninetail.utilities;

import jakarta.annotation.Nonnull;

import lombok.experimental.UtilityClass;

/**
 * Utility class for text formatting operations.
 */
@UtilityClass
public final class TextFormat {
    /**
     * Formats a string into verbatim text in Markdown.
     * 
     * @param s The string to make verbatim
     * 
     * @return The string with '`' surrounding it
     */
    public String markdownVerbatim(@Nonnull String s) {
        return String.format("`%s`", s);
    }
}
