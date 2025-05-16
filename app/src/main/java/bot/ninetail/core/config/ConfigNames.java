package bot.ninetail.core.config;

import jakarta.annotation.Nonnull;

public class ConfigNames {
    // Files
    public static final @Nonnull String CONFIG_PROPERTIES_FILE = "config.properties";
    public static final @Nonnull String RESPONSES_FILE = "responses.json";

    // Formattable file names
    public static final @Nonnull String LIST_GUILDS_NAME = "guildlist_%s.txt";
    public static final @Nonnull String LOG_FILE_NAME = "log_%s.txt";

    // Engines
    public static final @Nonnull String ENGINE_CHESS_PATH = "src/native/chess/lib/libchess.so";
    public static final @Nonnull String ENGINE_POKER_PATH = "src/native/chess/lib/libpoker.so";
}
