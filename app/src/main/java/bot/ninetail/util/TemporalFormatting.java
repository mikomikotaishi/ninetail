package bot.ninetail.util;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.Nonnull;

import lombok.experimental.UtilityClass;

/**
 * Utility class for temporal formatting operations.
 */
@UtilityClass
public final class TemporalFormatting {
    @Nonnull
    public static final DateTimeFormatter FILE_NAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @Nonnull
    public static final DateTimeFormatter LOG_ENTRY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Gets a formatted time string.
     *
     * @param msDuration The duration in milliseconds.
     * 
     * @return The formatted time string.
     */
    public static String getFormattedTime(long msDuration) {
        Duration duration = Duration.ofMillis(msDuration);
        long hours = duration.toHours();
        long remainingMinutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, remainingMinutes, seconds);
        }
        return String.format("%d:%02d", remainingMinutes, seconds);
    }
}
