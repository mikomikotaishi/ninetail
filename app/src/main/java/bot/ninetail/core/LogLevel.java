package bot.ninetail.core;

import jakarta.annotation.Nonnull;

/**
 * Enum representing the levels (types) of logs to make on the Logger
 */
public enum LogLevel {
    INFO(1, "INFO"),
    WARN(2, "WARN"),
    ERROR(3, "ERROR"),
    DEBUG(0, "DEBUG");
    
    private final int priority;
    private final String displayName;
    
    /**
     * Constructs a LogLevel object
     * 
     * @param priority The priority
     * @param displayName The display name
     */
    LogLevel(int priority, @Nonnull String displayName) {
        this.priority = priority;
        this.displayName = displayName;
    }
    
    /**
     * Obtain the priority of a LogLevel.
     * 
     * @return The priority
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Obtain the display name of a LogLevel.
     * 
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Compare a LogLevel's seriousness/level.
     * 
     * @param other The other LogLevel
     * 
     * @return True if other.priority is at least as serious, false otherwise
     */
    public boolean isAtLeastAsSerious(LogLevel other) {
        return this.priority >= other.priority;
    }
}
