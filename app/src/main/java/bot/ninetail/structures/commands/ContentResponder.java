package bot.ninetail.structures.commands;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.utilities.RandomNumberGenerator;

/**
 * Class that holds an array of strings containing contents, to be queried.
 */
public abstract class ContentResponder {
    /**
     * Maps each ContentResponder subclass to its specific contents.
     */
    private static final Map<Class<? extends ContentResponder>, String[]> CLASS_CONTENTS = new HashMap<>();
    
    /**
     * Sets the contents for a specific ContentResponder subclass.
     * This should be called in the static initialiser of each subclass.
     * 
     * @param clazz The class to set contents for
     * @param contents The contents array
     */
    protected static void setContents(Class<? extends ContentResponder> clazz, String[] contents) {
        CLASS_CONTENTS.put(clazz, contents);
    }

    /**
     * Returns the contents for the calling class.
     * 
     * @return The contents array for the calling class
     */
    protected static String[] getContents() {
        Class<?> callerClass = getCallerClass();
        return CLASS_CONTENTS.getOrDefault(callerClass, new String[0]);
    }

    /**
     * Retrieves a random item from the calling class's contents.
     * 
     * @return A random item from contents
     */
    protected static String getRandomContent() {
        String[] contents = getContents();
        if (contents.length == 0) {
            return "";
        }
        return contents[RandomNumberGenerator.generateRandomNumber(contents.length)];
    }
    
    /**
     * Gets the class that called this method.
     * 
     * @return The calling class
     */
    private static Class<? extends ContentResponder> getCallerClass() {
        @Nonnull String unfoundClass = "";
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            try {
                Class<?> clazz = Class.forName(className);
                if (ContentResponder.class.isAssignableFrom(clazz) && clazz != ContentResponder.class) {
                    return clazz.asSubclass(ContentResponder.class);
                }
            } catch (ClassNotFoundException e) {
                unfoundClass = className;
                Logger.log(LogLevel.WARN, "Warning: class not found: " + unfoundClass);
            }
        }
        Logger.log(LogLevel.ERROR, String.format("Failed to determine caller class: No ContentResponder subclass (%s) found in stack trace", unfoundClass));
        return null;
    }
}
