package bot.ninetail.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import bot.ninetail.core.config.ConfigNames;
import bot.ninetail.core.config.ConfigPaths;
import bot.ninetail.utilities.TemporalFormatting;

/**
 * Class to handle logging bot messages.
 */
public class Logger {
    private static BufferedWriter writer;

    static {
        try {
            File logDir = new File(ConfigPaths.LOG_PATH);
            if (!logDir.exists())
                logDir.mkdirs();
            
            String fileName = String.format(ConfigNames.LOG_FILE_NAME, LocalDateTime.now().format(TemporalFormatting.FILE_NAME_FORMAT));
            File logFile = new File(logDir, fileName);

            writer = new BufferedWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            System.err.println("Failed to initialise logger: " + e.getMessage());
        }
    }

    /**
     * Logs a message to the log file.
     * 
     * @param level
     * @param message
     */
    public static void log(LogLevel level, String message) {
        try {
            if (writer != null) {
                String timestamp = LocalDateTime.now().format(TemporalFormatting.LOG_ENTRY_FORMAT);
                writer.write(String.format("[%s] [%s] %s%n", timestamp, level, message));
                writer.flush();
            } else {
                throw new IllegalStateException("Writer not initialised.");
            }
        } catch (IllegalStateException e) {
            System.err.println("Failed to write to log file, improperly initialised writer: " + e.getMessage()); 
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    /**
     * Logs an exception's stack trace to the log file.
     * 
     * @param level The log level (e.g., ERROR, WARN).
     * @param exception The exception to log.
     */
    public static void logException(LogLevel level, Exception exception) {
        try {
            if (writer != null) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                exception.printStackTrace(printWriter);
                printWriter.flush();

                String timestamp = LocalDateTime.now().format(TemporalFormatting.LOG_ENTRY_FORMAT);
                writer.write(String.format("[%s] [%s] %s%n", timestamp, level, stringWriter.toString()));
                writer.flush();
            } else {
                throw new IllegalStateException("Writer not initialised.");
            }
        } catch (IllegalStateException e) {
            System.err.println("Failed to write to log file, improperly initialised writer: " + e.getMessage()); 
        } catch (IOException e) {
            System.err.println("Failed to write exception to log file: " + e.getMessage());
        }
    }

    /**
     * Closes the logger and releases resources.
     */
    public static void close() {
        try {
            if (writer != null)
                writer.close();
        } catch (IOException e) {
            System.err.println("Failed to close logger: " + e.getMessage());
        }
    }
}
