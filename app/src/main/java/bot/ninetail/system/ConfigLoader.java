package bot.ninetail.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.config.ConfigNames;
import bot.ninetail.core.logger.*;

import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * Loads configuration properties from config.properties.
 */
@UtilityClass
public final class ConfigLoader {
    /**
     * Properties object to store configuration properties.
     */
    @Nonnull 
    private final Properties PROPERTIES = new Properties();

    // === System ===
    /**
     * The bot token.
     */
    @Getter 
    @Nonnull 
    private String botToken;

    /**
     * The master password.
     */
    @Getter 
    @Nonnull 
    private String masterPassword;

    /**
     * The bot master ID.
     */
    @Getter 
    @Nonnull 
    private String botMasterId;

    /**
     * The username to the coins registry database.
     */
    @Getter 
    @Nonnull 
    private String coinsRegistryDbUsername;

    /**
     * The password to the coins registry database.
     */
    @Getter 
    @Nonnull 
    private String coinsRegistryDbPassword;

    /**
     * The URL of the coins registry database.
     */
    @Getter 
    @Nonnull 
    private String coinsRegistryDbUrl;

    /**
     * List of pre-banned user IDs from config.
     */
    @Getter 
    @Nonnull 
    private List<Long> prebannedUserIds;

    // === General ===
    /**
     * The weather token.
     */
    @Getter 
    @Nonnull 
    private String weatherToken;

    // === Imageboards ===
    /**
     * The Danbooru token.
     */
    @Getter 
    @Nonnull 
    private String danbooruToken;

    /**
     * The e621 token.
     */
    @Getter 
    @Nonnull 
    private String e621Token;

    /**
     * The Gelbooru token.
     */
    @Getter 
    @Nonnull 
    private String gelbooruToken;

    /**
     * The Gyate Booru token.
     */
    @Getter 
    @Nonnull 
    private String gyateBooruToken;

    /**
     * The Rule34 token.
     */
    @Getter 
    @Nonnull 
    private String rule34Token;

    /**
     * Static block to load properties from config.properties.
     */
    static {
        load(false);
    }

    /**
     * Reloads (or initially loads) configuration values.
     *
     * @param verbose if {@code true}, also re-validate the mandatory keys and issue warnings.
     */
    public static void reloadConfig() {
        load(true);
    }

    /**
     * Load the contents from content.properties.
     * 
     * @param verbose
     */
    private static void load(boolean verbose) {
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(ConfigNames.CONFIG_PROPERTIES_FILE)) {

            if (input == null) {
                String msg = "Unable to find " + ConfigNames.CONFIG_PROPERTIES_FILE;
                Logger.log(LogLevel.ERROR, msg);
                throw new FileNotFoundException(msg);
            }

            PROPERTIES.load(input);

            botToken = PROPERTIES.getProperty("BOT_TOKEN");
            masterPassword = PROPERTIES.getProperty("MASTER_PASSWORD");
            botMasterId = PROPERTIES.getProperty("BOT_MASTER_ID");
            coinsRegistryDbUsername = PROPERTIES.getProperty("COINS_REGISTRY_DB_USERNAME");
            coinsRegistryDbPassword = PROPERTIES.getProperty("COINS_REGISTRY_DB_PASSWORD");
            coinsRegistryDbUrl = PROPERTIES.getProperty("COINS_REGISTRY_DB_URL");

            // Load pre-banned user IDs
            String bannedIdsString = PROPERTIES.getProperty("PREBANNED_USER_IDS");
            if (bannedIdsString != null && !bannedIdsString.trim().isEmpty()) {
                try {
                    prebannedUserIds = Arrays.stream(bannedIdsString.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                    
                    if (verbose) {
                        Logger.log(LogLevel.INFO, "Loaded %d pre-banned user IDs from config", prebannedUserIds.size());
                    }
                } catch (NumberFormatException e) {
                    Logger.log(LogLevel.ERROR, "Invalid format in PREBANNED_USER_IDS: %s", e.getMessage());
                    prebannedUserIds = List.of(); // Empty list on error
                }
            } else {
                prebannedUserIds = List.of(); // Empty list if not specified
            }

            weatherToken = PROPERTIES.getProperty("WEATHER_TOKEN");

            danbooruToken = PROPERTIES.getProperty("DANBOORU_TOKEN");
            e621Token = PROPERTIES.getProperty("E621_TOKEN");
            gelbooruToken = PROPERTIES.getProperty("GELBOORU_TOKEN");
            gyateBooruToken = PROPERTIES.getProperty("GYATEBOORU_TOKEN");
            rule34Token = PROPERTIES.getProperty("RULE34_TOKEN");

            if (!verbose) {
                return;
            }

            if (botToken == null) {
                throwRequired("bot token");
            }
            if (masterPassword == null) {
                throwRequired("master password");
            }

            warnIfNull(weatherToken, "Weather");
            warnIfNull(danbooruToken, "Danbooru");
            warnIfNull(e621Token, "e621");
            warnIfNull(gelbooruToken, "Gelbooru");
            warnIfNull(gyateBooruToken ,"Gyate Booru");
            warnIfNull(rule34Token, "Rule34");

        } catch (IOException e) {
            Logger.logException(LogLevel.ERROR, e);
        }
    }

    /**
     * Throws an exception due to an illegal argument/missing item.
     * 
     * @param what The missing item.
     */
    private static void throwRequired(String what) {
        Logger.log(LogLevel.ERROR, "No %s found!", what);
        throw new IllegalArgumentException(String.format("No %s found!", what));
    }

    /**
     * Issues a warning to the logger about a potentially null field.
     * 
     * @param token The token to check for null.
     * @param name The name of the token.
     */
    private static void warnIfNull(String token, String name) {
        if (token == null) {
            Logger.log(LogLevel.WARN, "No %s token found!", name);
        }
    }
}
