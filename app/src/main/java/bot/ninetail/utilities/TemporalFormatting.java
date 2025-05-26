package bot.ninetail.utilities;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.Nonnull;

import lombok.experimental.UtilityClass;

/**
 * Utility class for temporal formatting operations.
 */
@UtilityClass
public class TemporalFormatting {
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
        String formattedDuration;
        if (hours > 0)
            formattedDuration = String.format("%d:%02d:%02d", hours, remainingMinutes, seconds);
        else
            formattedDuration = String.format("%d:%02d", remainingMinutes, seconds);
        return formattedDuration;
    }
}
