package bot.ninetail.game;

import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;

/**
 * Abstract Engine class.
 * This class is used to load libraries for engines.
 */
public abstract class Engine {
    @Nonnull private final String libraryName;

    public Engine(String libraryName) {
        this.libraryName = libraryName;
        String os = System.getProperty("os.name").toLowerCase();
        String libName = os.contains("win") ? String.format("%s.dll", libraryName) : String.format("lib%s.so", libraryName);
        Path libPath = Paths.get(String.format("src/native/%s/lib", libraryName), libName).toAbsolutePath();
        Logger.log(LogLevel.INFO, "Loading library at " + libPath.toString());
        System.load(libPath.toString());
    }
}
