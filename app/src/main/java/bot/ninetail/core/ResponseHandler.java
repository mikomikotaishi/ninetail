package bot.ninetail.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.annotation.Nonnull;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import bot.ninetail.core.config.ConfigNames;
import bot.ninetail.structures.InteractionHandler;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Class to handle responses to messages.
 * This class is used to send responses to messages based on keywords.
 * 
 * @extends InteractionHandler
 */
@UtilityClass
public final class ResponseHandler extends InteractionHandler {
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
        Logger.log(LogLevel.INFO, "Emptying map");
        RESPONSES.clear();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(ConfigNames.RESPONSES_FILE)) {
            if (inputStream == null) {
                Logger.log(LogLevel.WARN, ConfigNames.RESPONSES_FILE + " not found. Skipping loading responses.");
                return;
            }
            Logger.log(LogLevel.INFO, "Loading " + ConfigNames.RESPONSES_FILE);
            try (JsonReader jsonReader = Json.createReader(inputStream)) {
                JsonObject responsesJson = jsonReader.readObject();
                for (String key: responsesJson.keySet()) {
                    Logger.log(LogLevel.INFO, "Loading response for key " + key);
                    RESPONSES.put(key, responsesJson.getString(key));
                }
            }
        } catch (IOException e) {
            Logger.logException(LogLevel.ERROR, e);
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
