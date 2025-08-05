package bot.ninetail.clients;

import java.io.IOException;
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.http.*;

import jakarta.annotation.Nonnull;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import bot.ninetail.structures.clients.ImageboardClient;
import bot.ninetail.system.ConfigLoader;

/**
 * Client for e621.
 * 
 * @extends ImageboardClient
 */
public class E621Client extends ImageboardClient {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(E621Client.class.getName());

    /**
     * The base URL for e621.
     */
    @Nonnull
    private static final String BASE_URL = "https://e621.net/posts.json?tags=%s&api_key=%s";

    /**
     * Constructs a new e621 client.
     */
    public E621Client() {
        super(BASE_URL, ConfigLoader.getE621Token());
    }

    /**
     * Retrieves posts from e621.
     *
     * @param tag1 The first tag.
     * @param tag2 The second tag.
     * 
     * @return The posts.
     * 
     * @throws IOException If an error occurs while retrieving the posts.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public JsonArray getPosts(@Nonnull String tag1, String tag2) throws IOException, InterruptedException {
        if (getApiKey() == null || getApiKey().isEmpty()) {
            LOGGER.log(Level.ERROR, "e621 API key missing!");
            throw new IllegalArgumentException("No e621 token found!");
        }

        String tags = tag1;
        if (tag2 != null && !tag2.isEmpty()) {
            tags += ("+" + tag2);
        }
        
        String url = String.format(BASE_URL, tags, getApiKey());
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        LOGGER.log(Level.INFO, "Issuing request to e621 for tags: {0}", tags);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.log(Level.INFO, "Obtaining response.");
        if (response.statusCode() != 200) {
            LOGGER.log(Level.ERROR, "Failed to execute HTTP request!");
            throw new IOException("Failed to execute HTTP request");
        }
        LOGGER.log(Level.INFO, "Successfully obtained response.");
        String responseBody = response.body();
        
        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            JsonObject jsonResponse = jsonReader.readObject();
            if (!jsonResponse.containsKey("posts")) {
                LOGGER.log(Level.ERROR, "No 'posts' key in e621 response");
                return Json.createArrayBuilder().build();
            }
            return jsonResponse.getJsonArray("posts");
        }
    }
}
