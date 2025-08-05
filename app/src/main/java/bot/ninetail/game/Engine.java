package bot.ninetail.game;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.config.*;

/**
 * Abstract Engine class.
 * This class is used to load libraries for engines.
 */
public abstract class Engine {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Engine.class.getName());

    /**
     * The name of the library used by the engine.
     */
    @Nonnull 
    private final String libraryName;

    /**
     * Constructor for an Engine
     * 
     * @param libraryName The library name
     */
    public Engine(String libraryName) {
        this.libraryName = libraryName;

        String os = System.getProperty("os.name").toLowerCase();
        String libName = os.contains("win") ? String.format(ConfigNames.DLL_FILE_NAME, libraryName) : String.format(ConfigNames.SO_FILE_NAME, libraryName);
        Path libPath = Paths.get(String.format(ConfigPaths.LIB_PATH, libraryName), libName).toAbsolutePath();
        LOGGER.log(Level.INFO, "Loading library at {0}", libPath.toString());
        System.load(libPath.toString());
    }
}
