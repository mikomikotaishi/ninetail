package bot.ninetail.core.config;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.ConfigFile;

import lombok.experimental.UtilityClass;

/**
 * Class containing file names used by the bot.
 */
@UtilityClass
public final class ConfigNames extends ConfigFile {
    // Files
    @Nonnull 
    public static final String CONFIG_PROPERTIES_FILE = "config.properties";

    @Nonnull 
    public static final String RESPONSES_FILE = "responses.json";

    // Formattable file names
    @Nonnull 
    public static final String DLL_FILE_NAME = "%s.dll";

    @Nonnull 
    public static final String SO_FILE_NAME = "lib%s.so";

    @Nonnull 
    public static final String LIST_GUILDS_NAME = "guildlist_%s.txt";

    @Nonnull 
    public static final String LOG_FILE_NAME = "log_%s.txt";

    // Engines
    @Nonnull 
    public static final String ENGINE_CHESS_PATH = "src/native/chess/lib/libchess.so";

    @Nonnull 
    public static final String ENGINE_POKER_PATH = "src/native/chess/lib/libpoker.so";
}
