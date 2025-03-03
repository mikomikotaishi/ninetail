package bot.ninetail.structures.clients;

import java.io.IOException;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;

import bot.ninetail.structures.Client;

/**
 * Abstract class for imageboard clients.
 * This abstract class provides a base structure for grabbing posts from imageboards.
 * 
 * @extends Client
 */
public abstract class ImageboardClient extends Client {
    /**
     * Constructs a new ImageboardClient.
     *
     * @param baseUrl The base URL of the client.
     * @param apiKey The API key of the client.
     */
    protected ImageboardClient(@Nonnull String baseUrl, @Nonnull String apiKey) {
        super(baseUrl, apiKey);
    }

    /**
     * Gets posts from the imageboard.
     * @param tag1 The first tag to search for.
     * @param tag2 The second tag to search for (optional).
     * @return A JsonArray containing the posts.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    public JsonArray getPosts(@Nonnull String tag1, String tag2) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("This method needs to be implemented by subclasses");
    }
}
