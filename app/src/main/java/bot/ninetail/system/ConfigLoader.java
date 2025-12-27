package bot.ninetail.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.config.ConfigNames;

import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * Loads configuration properties from config.properties.
 */
@UtilityClass
public final class ConfigLoader {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(ConfigLoader.class.getName());

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
     * The Google Gemini token.
     */
    @Getter
    @Nonnull
    private String geminiToken;

    /**
     * The weather token.
     */
    @Getter 
    @Nonnull 
    private String weatherToken;

    // === Imageboards ===
    /**
     * The Danbooru login (username).
     */
    @Getter 
    @Nonnull 
    private String danbooruLogin;

    /**
     * The Danbooru token.
     */
    @Getter 
    @Nonnull 
    private String danbooruToken;

    /**
     * The e621 login (username).
     */
    @Getter 
    @Nonnull 
    private String e621Login;

    /**
     * The e621 token.
     */
    @Getter 
    @Nonnull 
    private String e621Token;

    /**
     * The Gelbooru login (user ID).
     */
    @Getter 
    @Nonnull 
    private String gelbooruLogin;

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
     * The Rule34 login (user ID).
     */
    @Getter 
    @Nonnull 
    private String rule34Login;

    /**
     * The Rule34 token.
     */
    @Getter 
    @Nonnull 
    private String rule34Token;

    /**
     * The Derpibooru login (user ID).
     */
    @Getter 
    @Nonnull 
    private String derpibooruLogin;

    /**
     * The Derpibooru token.
     */
    @Getter 
    @Nonnull 
    private String derpibooruToken;

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
                LOGGER.log(Level.ERROR, msg);
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
                        LOGGER.log(Level.INFO, "Loaded {0} pre-banned user IDs from config", prebannedUserIds.size());
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.ERROR, "Invalid format in PREBANNED_USER_IDS: {0}", e.getMessage());
                    prebannedUserIds = List.of(); // Empty list on error
                }
            } else {
                prebannedUserIds = List.of(); // Empty list if not specified
            }

            geminiToken = PROPERTIES.getProperty("OPENAI_TOKEN");
            weatherToken = PROPERTIES.getProperty("WEATHER_TOKEN");

            danbooruLogin = PROPERTIES.getProperty("DANBOORU_LOGIN");
            danbooruToken = PROPERTIES.getProperty("DANBOORU_TOKEN");
            e621Login = PROPERTIES.getProperty("E621_LOGIN");
            e621Token = PROPERTIES.getProperty("E621_TOKEN");
            gelbooruLogin = PROPERTIES.getProperty("GELBOORU_LOGIN");
            gelbooruToken = PROPERTIES.getProperty("GELBOORU_TOKEN");
            gyateBooruToken = PROPERTIES.getProperty("GYATEBOORU_TOKEN");
            rule34Login = PROPERTIES.getProperty("RULE34_LOGIN");
            rule34Token = PROPERTIES.getProperty("RULE34_TOKEN");
            derpibooruLogin = PROPERTIES.getProperty("DERPIBOORU_LOGIN");
            derpibooruToken = PROPERTIES.getProperty("DERPIBOORU_TOKEN");

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
            warnIfNull(danbooruLogin, "Danbooru login");
            warnIfNull(danbooruToken, "Danbooru token");
            warnIfNull(e621Login, "e621 login");
            warnIfNull(e621Token, "e621 token");
            warnIfNull(gelbooruLogin, "Gelbooru login");
            warnIfNull(gelbooruToken, "Gelbooru token");
            warnIfNull(gyateBooruToken ,"Gyate Booru");
            warnIfNull(rule34Login, "Rule34 login");
            warnIfNull(rule34Token, "Rule34 token");
            warnIfNull(derpibooruToken, "Derpibooru login");
            warnIfNull(derpibooruToken, "Derpibooru token");

        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Exception occurred while loading config: {0}", e.getMessage());
        }
    }

    /**
     * Throws an exception due to an illegal argument/missing item.
     * 
     * @param what The missing item.
     */
    private static void throwRequired(String what) {
        LOGGER.log(Level.ERROR, "No {0} found!", what);
        throw new IllegalArgumentException(String.format("No %s found!", what));
    }

    /**
     * Issues a warning to the LOGGER about a potentially null field.
     * 
     * @param token The token to check for null.
     * @param name The name of the token.
     */
    private static void warnIfNull(String token, String name) {
        if (token == null) {
            LOGGER.log(Level.WARNING, "No {0} token found!", name);
        }
    }
}
