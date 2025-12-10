package bot.ninetail.structures.clients;

import java.io.IOException;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;

import bot.ninetail.structures.Client;

/**
 * Abstract class for AI clients.
 * This abstract class provides a base structure for accessing AI APIs.
 * 
 * @extends Client
 */
public abstract class AIClient extends Client {
    /**
     * Constructs a new AIClient.
     *
     * @param apiKey The API key of the client.
     */
    protected AIClient(@Nonnull String apiKey) {
        super("google.com", apiKey);
    }
}
