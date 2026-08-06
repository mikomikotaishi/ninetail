package bot.ninetail.audio;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.annotation.Nonnull;

import lombok.experimental.UtilityClass;

/**
 * Utility class for turning audio failures into something that can be shown to a user.
 */
@UtilityClass
public final class AudioErrors {
    /**
     * The prefix of the lines that the YouTube source uses to report why an individual client
     * could not serve a track.
     */
    @Nonnull
    private static final String CLIENT_FAILURE_PREFIX = "Client [";

    /**
     * The maximum length of a summary.
     * Failures from the YouTube source carry a stack trace per client, which is far longer than
     * a message may be.
     */
    private static final int MAX_SUMMARY_LENGTH = 300;

    /**
     * Summarizes why a track could not be loaded or played.
     *
     * @param exception The failure.
     * @return A short description of the failure.
     */
    @Nonnull
    public static String summarize(@Nonnull Throwable exception) {
        Set<String> reasons = new LinkedHashSet<>();
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            String message = cause.getMessage();
            if (message == null) {
                continue;
            }
            for (String line: message.split("\n")) {
                if (line.startsWith(CLIENT_FAILURE_PREFIX)) {
                    reasons.add(line.trim());
                }
            }
        }

        String summary = reasons.isEmpty() ? firstLine(exception) : String.join("; ", reasons);
        return summary.length() > MAX_SUMMARY_LENGTH
            ? String.format("%s...", summary.substring(0, MAX_SUMMARY_LENGTH))
            : summary;
    }

    /**
     * Gets the first line of a failure's message.
     *
     * @param exception The failure.
     * @return The first line of the message, or the failure's type if it carries no message.
     */
    @Nonnull
    private static String firstLine(@Nonnull Throwable exception) {
        String message = exception.getMessage();
        return message == null ? exception.getClass().getSimpleName() : message.split("\n")[0];
    }
}
