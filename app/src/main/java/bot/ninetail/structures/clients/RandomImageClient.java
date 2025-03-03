package bot.ninetail.structures.clients;

import java.io.IOException;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;

import bot.ninetail.structures.Client;

/**
 * Abstract class for random image clients.
 * This abstract class provides a base structure for grabbing random images from random image services.
 */
public abstract class RandomImageClient extends Client {
    /**
     * Constructs a new RandomImageClient.
     *
     * @param baseUrl The base URL of the client.
     * @param apiKey The API key of the client.
     */
    protected RandomImageClient(@Nonnull String baseUrl, @Nonnull String apiKey) {
        super(baseUrl, apiKey);
    }

    /**
     * Gets a random image from the service.
     * @return A JsonArray containing the image data.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    public JsonArray getImage() throws IOException, InterruptedException {
        throw new UnsupportedOperationException("This method needs to be implemented by subclasses");
    }
}
