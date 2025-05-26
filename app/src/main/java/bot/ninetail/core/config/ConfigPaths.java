package bot.ninetail.core.config;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.ConfigFile;

import lombok.Getter;

/**
 * Class containing paths used by the bot.
 */
@Getter
public final class ConfigPaths extends ConfigFile {
    /**
     * Private constructor to prevent instantiation.
     */
    private ConfigPaths() {}

    @Nonnull public static final String LIB_PATH = "src/native/%s/lib";
    @Nonnull public static final String LOG_PATH = "./logs";
}
