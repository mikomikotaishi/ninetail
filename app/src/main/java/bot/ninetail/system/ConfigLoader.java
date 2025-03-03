package bot.ninetail.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads configuration properties from config.properties.
 */
public class ConfigLoader {
    /**
     * Properties object to store configuration properties.
     */
    private static final Properties PROPERTIES = new Properties();

    // System
    /**
     * The bot token.
     */
    private static String botToken;

    /**
     * The master password.
     */
    private static String masterPassword;

    // General
    /**
     * The weather token.
     */
    private static String weatherToken;

    // Imageboards
    /**
     * The Danbooru token.
     */
    private static String danbooruToken;

    /**
     * The e621 token.
     */
    private static String e621Token;

    /**
     * The Gelbooru token.
     */
    private static String gelbooruToken;

    /**
     * The Gyate Booru token.
     */
    private static String gyatebooruToken;

    /**
     * The Rule34 token.
     */
    private static String rule34Token;

    /**
     * Static block to load properties from config.properties.
     */
    static {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Unable to find config.properties!");
                throw new FileNotFoundException("Unable to find config.properties!");
            }
            PROPERTIES.load(input);
            // System
            botToken = PROPERTIES.getProperty("BOT_TOKEN");
            masterPassword = PROPERTIES.getProperty("MASTER_PASSWORD");
            
            // General
            weatherToken = PROPERTIES.getProperty("WEATHER_TOKEN");

            // Imageboards
            danbooruToken = PROPERTIES.getProperty("DANBOORU_TOKEN");
            e621Token = PROPERTIES.getProperty("E621_TOKEN");
            gelbooruToken = PROPERTIES.getProperty("GELBOORU_TOKEN");
            gyatebooruToken = PROPERTIES.getProperty("GYATEBOORU_TOKEN");
            rule34Token = PROPERTIES.getProperty("RULE34_TOKEN");


            if (botToken == null) {
                System.err.println("No bot token found!");
                throw new IllegalArgumentException("No bot token found!");
            }
            if (masterPassword == null) {
                System.err.println("No shutdown password found!");
                throw new IllegalArgumentException("No shutdown password found!");
            }
            if (weatherToken == null)
                System.err.println("No Weather token found!");
            if (danbooruToken == null)
                System.err.println("No Danbooru token found!");
            if (e621Token == null)
                System.err.println("No e621 token found!");
            if (gelbooruToken == null)
                System.err.println("No Gelbooru token found!");
            if (gyatebooruToken == null)
                System.err.println("No Gyate Booru token found!");
            if (rule34Token == null)
                System.err.println("No Rule34 token found!");
            
                
        } catch (IOException e) {
            e.printStackTrace();
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
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Unable to find config.properties!");
                throw new FileNotFoundException("Unable to find config.properties!");
            }
            PROPERTIES.load(input);
            weatherToken = PROPERTIES.getProperty("WEATHER_TOKEN");
            danbooruToken = PROPERTIES.getProperty("DANBOORU_TOKEN");
            e621Token = PROPERTIES.getProperty("E621_TOKEN");
            gelbooruToken = PROPERTIES.getProperty("GELBOORU_TOKEN");
            gyatebooruToken = PROPERTIES.getProperty("GYATEBOORU_TOKEN");
            rule34Token = PROPERTIES.getProperty("RULE34_TOKEN");
            
            if (weatherToken == null)
                System.err.println("No weather token found!");
            if (danbooruToken == null)
                System.err.println("No Danbooru token found!");
            if (e621Token == null)
                System.err.println("No e621 token found!");
            if (gelbooruToken == null)
                System.err.println("No Gelbooru token found!");
            if (gyatebooruToken == null)
                System.err.println("No Gyate Booru token found!");
            if (rule34Token == null)
                System.err.println("No Rule34 token found!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}