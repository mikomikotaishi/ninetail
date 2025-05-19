package bot.ninetail.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.core.config.ConfigNames;

/**
 * Loads configuration properties from config.properties.
 */
public class ConfigLoader {
    /**
     * Properties object to store configuration properties.
     */
    @Nonnull private static final Properties PROPERTIES = new Properties();

    // System
    /**
     * The bot token.
     */
    @Nonnull private static String botToken;

    /**
     * The master password.
     */
    @Nonnull private static String masterPassword;

    /**
     * The bot master ID.
     */
    @Nonnull private static String botMasterId;

    /**
     * The username to the coins registry database.
     */
    @Nonnull private static String coinsRegistryDbUsername;

    /**
     * The password to the coins registry database.
     */
    @Nonnull private static String coinsRegistryDbPassword;

    /**
     * The URL of the coins registry database.
     */
    @Nonnull private static String coinsRegistryDbUrl;

    // General
    /**
     * The weather token.
     */
    @Nonnull private static String weatherToken;

    // Imageboards
    /**
     * The Danbooru token.
     */
    @Nonnull private static String danbooruToken;

    /**
     * The e621 token.
     */
    @Nonnull private static String e621Token;

    /**
     * The Gelbooru token.
     */
    @Nonnull private static String gelbooruToken;

    /**
     * The Gyate Booru token.
     */
    @Nonnull private static String gyatebooruToken;

    /**
     * The Rule34 token.
     */
    @Nonnull private static String rule34Token;

    /**
     * Static block to load properties from config.properties.
     */
    static {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(ConfigNames.CONFIG_PROPERTIES_FILE)) {
            if (input == null) {
                Logger.log(LogLevel.ERROR, String.format("Unable to find %s!", ConfigNames.CONFIG_PROPERTIES_FILE));
                throw new FileNotFoundException(String.format("Unable to find %s", ConfigNames.CONFIG_PROPERTIES_FILE));
            }
            PROPERTIES.load(input);
            // System
            botToken = PROPERTIES.getProperty("BOT_TOKEN");
            masterPassword = PROPERTIES.getProperty("MASTER_PASSWORD");
            botMasterId = PROPERTIES.getProperty("BOT_MASTER_ID");
            coinsRegistryDbUsername = PROPERTIES.getProperty("COINS_REGISTRY_DB_USERNAME");
            coinsRegistryDbPassword = PROPERTIES.getProperty("COINS_REGISTRY_DB_PASSWORD");
            coinsRegistryDbUrl = PROPERTIES.getProperty("COINS_REGISTRY_DB_URL");

            // General
            weatherToken = PROPERTIES.getProperty("WEATHER_TOKEN");

            // Imageboards
            danbooruToken = PROPERTIES.getProperty("DANBOORU_TOKEN");
            e621Token = PROPERTIES.getProperty("E621_TOKEN");
            gelbooruToken = PROPERTIES.getProperty("GELBOORU_TOKEN");
            gyatebooruToken = PROPERTIES.getProperty("GYATEBOORU_TOKEN");
            rule34Token = PROPERTIES.getProperty("RULE34_TOKEN");


            if (botToken == null) {
                Logger.log(LogLevel.ERROR, "No bot token found!");
                throw new IllegalArgumentException("No bot token found!");
            }
            if (masterPassword == null) {
                Logger.log(LogLevel.ERROR, "No shutdown password found!");
                throw new IllegalArgumentException("No shutdown password found!");
            }
            if (weatherToken == null)
                Logger.log(LogLevel.WARN, "No Weather token found!");
            if (danbooruToken == null)
                Logger.log(LogLevel.WARN, "No Danbooru token found!");
            if (e621Token == null)
                Logger.log(LogLevel.WARN, "No e621 token found!");
            if (gelbooruToken == null)
                Logger.log(LogLevel.WARN, "No Gelbooru token found!");
            if (gyatebooruToken == null)
                Logger.log(LogLevel.WARN, "No Gyate Booru token found!");
            if (rule34Token == null)
                Logger.log(LogLevel.WARN, "No Rule34 token found!");
            
                
        } catch (IOException e) {
            Logger.logException(LogLevel.ERROR, e);
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ConfigLoader() {}

    /**
     * Gets the bot token.
     *
     * @return The bot token.
     */
    public static String getBotToken() {
        return botToken;
    }

    /**
     * Gets the master password.
     *
     * @return The master password.
     */
    public static String getMasterPassword() {
        return masterPassword;
    }

    /**
     * Gets the bot master ID.
     *
     * @return The bot master ID.
     */
    public static String getBotMasterId() {
        return botMasterId;
    }

    /**
     * Gets the coins registry database username.
     * 
     * @return The coins registry database username.
     */
    public static String getCoinsRegistryDbUsername() {
        return coinsRegistryDbUsername;
    }

    /**
     * Gets the coins registry database password.
     * 
     * @return The coins registry database password.
     */
    public static String getCoinsRegistryDbPassword() {
        return coinsRegistryDbPassword;
    }

    /**
     * Gets the coins registry database URL.
     * 
     * @return The coins registry database URL.
     */
    public static String getCoinsRegistryDbUrl() {
        return coinsRegistryDbUrl;
    }

    /**
     * Gets the weather token.
     *
     * @return The weather token.
     */
    public static String getWeatherToken() {
        return weatherToken;
    }

    /**
     * Gets the Danbooru token.
     *
     * @return The Danbooru token.
     */
    public static String getDanbooruToken() {
        return danbooruToken;
    }

    /**
     * Gets the e621 token.
     *
     * @return The e621 token.
     */
    public static String getE621Token() {
        return e621Token;
    }

    /**
     * Gets the Gelbooru token.
     *
     * @return The Gelbooru token.
     */
    public static String getGelbooruToken() {
        return gelbooruToken;
    }

    /**
     * Gets the Gyate Booru token.
     *
     * @return The Gyate Booru token.
     */
    public static String getGyateBooruToken() {
        return gyatebooruToken;
    }

    /**
     * Gets the Rule34 token.
     *
     * @return The Rule34 token.
     */
    public static String getRule34Token() {
        return rule34Token;
    }

    /**
     * Reloads the configuration.
     */
    public static void reloadConfig() {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(ConfigNames.CONFIG_PROPERTIES_FILE)) {
            if (input == null) {
                Logger.log(LogLevel.ERROR, String.format("Unable to find %s!", ConfigNames.CONFIG_PROPERTIES_FILE));
                throw new FileNotFoundException(String.format("Unable to find %s!", ConfigNames.CONFIG_PROPERTIES_FILE));
            }
            PROPERTIES.load(input);
            weatherToken = PROPERTIES.getProperty("WEATHER_TOKEN");
            danbooruToken = PROPERTIES.getProperty("DANBOORU_TOKEN");
            e621Token = PROPERTIES.getProperty("E621_TOKEN");
            gelbooruToken = PROPERTIES.getProperty("GELBOORU_TOKEN");
            gyatebooruToken = PROPERTIES.getProperty("GYATEBOORU_TOKEN");
            rule34Token = PROPERTIES.getProperty("RULE34_TOKEN");
            
            if (weatherToken == null)
                Logger.log(LogLevel.WARN, "No weather token found!");
            if (danbooruToken == null)
                Logger.log(LogLevel.WARN, "No Danbooru token found!");
            if (e621Token == null)
                Logger.log(LogLevel.WARN, "No e621 token found!");
            if (gelbooruToken == null)
                Logger.log(LogLevel.WARN, "No Gelbooru token found!");
            if (gyatebooruToken == null)
                Logger.log(LogLevel.WARN, "No Gyate Booru token found!");
            if (rule34Token == null)
                Logger.log(LogLevel.WARN, "No Rule34 token found!");
        } catch (IOException e) {
            Logger.logException(LogLevel.ERROR, e);
        }
    }
}
