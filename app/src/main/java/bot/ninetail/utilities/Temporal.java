package bot.ninetail.utilities;

import java.time.Duration;

/**
 * Utility class for temporal operations.
 */
public class Temporal {
    /**
     * Private constructor to prevent instantiation.
     */
    private Temporal() {}

    /**
     * Gets a formatted time string.
     *
     * @param msDuration The duration in milliseconds.
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
