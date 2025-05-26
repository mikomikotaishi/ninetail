package bot.ninetail.core.config;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.ConfigFile;

import lombok.experimental.UtilityClass;

/**
 * Class containing paths used by the bot.
 */
@UtilityClass
public final class ConfigPaths extends ConfigFile {
    @Nonnull 
    public static final String LIB_PATH = "src/native/%s/lib";

    @Nonnull 
    public static final String LOG_PATH = "./logs";
}
