package bot.ninetail.utilities;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for temporal formatting operations.
 */
public class TemporalFormatting {
    /**
     * Private constructor to prevent instantiation.
     */
    private TemporalFormatting() {}

    public static final DateTimeFormatter FILE_NAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
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
