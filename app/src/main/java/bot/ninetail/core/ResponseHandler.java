package bot.ninetail.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.annotation.Nonnull;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import bot.ninetail.core.config.ConfigNames;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Class to handle responses to messages.
 * This class is used to send responses to messages based on keywords.
 */
@UtilityClass
public final class ResponseHandler {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(ResponseHandler.class.getName());
    
    /**
     * Map of keywords to responses.
     */
    @Nonnull 
    private static Map<String, String> RESPONSES = new HashMap<>();

    /**
     * Loads responses from responses.json.
     */
    static {
        reloadResponses();
    }

    /**
     * Reloads responses from responses.json.
     */
    public static void reloadResponses() {
        LOGGER.log(Level.INFO, "Emptying map");
        RESPONSES.clear();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(ConfigNames.RESPONSES_FILE)) {
            if (inputStream == null) {
                LOGGER.log(Level.WARNING, "{0} not found. Skipping loading responses.", ConfigNames.RESPONSES_FILE);
                return;
            }
            LOGGER.log(Level.INFO, "Loading {0}", ConfigNames.RESPONSES_FILE);
            try (JsonReader jsonReader = Json.createReader(inputStream)) {
                JsonObject responsesJson = jsonReader.readObject();
                for (String key: responsesJson.keySet()) {
                    LOGGER.log(Level.INFO, "Loading response for key {0}", key);
                    RESPONSES.put(key, responsesJson.getString(key));
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Exception occurred while loading responses: {0}", e.getMessage());
        }
    }

    /**
     * Handles a message by sending a response if a keyword is found.
     * 
     * @param content The message content.
     * @param textChannel The channel to send the response to.
     */
    public static void handleMessage(String content, MessageChannel textChannel) {
        for (Map.Entry<String, String> entry: RESPONSES.entrySet()) {
            String pattern = String.format("\\b%s\\b", Pattern.quote(entry.getKey()));
            if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(content).find()) {
                textChannel.sendMessage(entry.getValue()).queue();
                break;
            }
        }
    }
}
