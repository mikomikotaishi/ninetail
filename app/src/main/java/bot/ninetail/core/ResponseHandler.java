package bot.ninetail.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Class to handle responses to messages.
 * This class is used to send responses to messages based on keywords.
 */
public class ResponseHandler {
    /**
     * Private constructor to prevent instantiation.
     */
    private ResponseHandler() {}

    /**
     * Map of keywords to responses.
     */
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
        System.out.println("Emptying map");
        RESPONSES.clear();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("responses.json")) {
            if (inputStream == null) {
                System.out.println("responses.json not found. Skipping loading responses.");
                return;
            }
            System.out.println("Loading responses.json");
            try (JsonReader jsonReader = Json.createReader(inputStream)) {
                JsonObject responsesJSON = jsonReader.readObject();
                for (String key : responsesJSON.keySet()) {
                    System.out.println("Loading response for key " + key);
                    RESPONSES.put(key, responsesJSON.getString(key));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a message by sending a response if a keyword is found.
     * @param content The message content.
     * @param textChannel The channel to send the response to.
     */
    public static void handleMessage(String content, MessageChannel textChannel) {
        System.out.println("Parsing for responses");
        System.out.println("Message: " + content);
        for (Map.Entry<String, String> entry: RESPONSES.entrySet()) {
            System.out.println("Checking response key: " + entry.getKey());
            String pattern = String.format("\\b%s\\b", Pattern.quote(entry.getKey()));
            if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(content).find()) {
                System.out.println("Match found! Sending: " + entry.getValue());
                textChannel.sendMessage(entry.getValue()).queue();
                break;
            }
        }
    }
}