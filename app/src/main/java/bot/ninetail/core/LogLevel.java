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
    
    LogLevel(int priority, @Nonnull String displayName) {
        this.priority = priority;
        this.displayName = displayName;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isAtLeastAsSerious(LogLevel other) {
        return this.priority >= other.priority;
    }
}
