package bot.ninetail.system.logging;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.config.ConfigNames;
import bot.ninetail.core.config.ConfigPaths;
import bot.ninetail.util.TemporalFormatting;

import lombok.experimental.UtilityClass;

/**
 * Configuration utility for System.Logger with JUL backend.
 */
@UtilityClass
public final class LoggerConfig {
    @Nonnull
    private static FileHandler fileHandler;
    private static boolean configured = false;
    
    /**
     * Configure all loggers in the bot.ninetail hierarchy.
     */
    public static synchronized void configure() {
        if (configured) {
            return;
        }
        
        try {
            File logDir = new File(ConfigPaths.LOG_PATH);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            String fileName = String.format(
                ConfigNames.LOG_FILE_NAME, 
                LocalDateTime.now().format(TemporalFormatting.FILE_NAME_FORMAT)
            );
            File logFile = new File(logDir, fileName);
            
            fileHandler = new FileHandler(logFile.getAbsolutePath(), true);
            fileHandler.setFormatter(new NinetailLoggerFormatter());
            
            Logger rootLogger = Logger.getLogger("bot.ninetail");
            rootLogger.setLevel(Level.ALL);
            rootLogger.addHandler(fileHandler);
            rootLogger.setUseParentHandlers(false);
            
            configured = true;
        } catch (IOException e) {
            System.err.println("Failed to initialise logger: " + e.getMessage());
        }
    }
    
    /**
     * Close the logger and release resources.
     */
    public static void close() {
        if (fileHandler != null) {
            fileHandler.close();
        }
    }
    
    /**
     * Custom formatter to match the existing log format.
     */
    private static class NinetailLoggerFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String timestamp = LocalDateTime.now().format(TemporalFormatting.LOG_ENTRY_FORMAT);
            String level = record.getLevel().getName();
            String message = formatMessage(record);
            String className = record.getSourceClassName();
            
            if (className != null) {
                className = className.substring(className.lastIndexOf('.') + 1);
                return String.format("[%s] [%s] [%s] %s%n", timestamp, level, className, message);
            } else {
                return String.format("[%s] [%s] %s%n", timestamp, level, message);
            }
        }
    }
}