package bot.ninetail.clients;

import java.io.IOException;
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.Nonnull;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;

import bot.ninetail.structures.clients.ImageboardClient;
import bot.ninetail.system.ConfigLoader;

/**
 * Client for Danbooru.
 * 
 * @extends ImageboardClient
 */
public class DanbooruClient extends ImageboardClient {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(DanbooruClient.class.getName());

    /**
     * The base URL for Danbooru.
     */
    @Nonnull
    private static final String BASE_URL = "https://danbooru.donmai.us/posts.json?tags=%s&login=%s&api_key=%s";

    /**
     * Constructs a new Danbooru client.
     */
    public DanbooruClient() {
        super(BASE_URL, ConfigLoader.getDanbooruLogin(), ConfigLoader.getDanbooruToken(), 1, 1000);
    }
    
    /**
     * Retrieves posts from Danbooru.
     *
     * @param tag1 The first tag.
     * @param tag2 The second tag.
     * @return The posts.
     * 
     * @throws IOException If an error occurs while retrieving the posts.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public JsonArray getPosts(@Nonnull String tag1, String tag2) throws IOException, InterruptedException {
        if (getApiKey() == null || getApiKey().isEmpty()) {
            LOGGER.log(Level.ERROR, "Danbooru API key missing!");
            throw new IllegalArgumentException("No Danbooru token found!");
        }

        if (getLogin() == null || getLogin().isEmpty()) {
            LOGGER.log(Level.ERROR, "Danbooru login missing!");
            throw new IllegalArgumentException("No Danbooru login found!");
        }

        waitForRateLimit();

        String tags = tag1;
        if (tag2 != null && !tag2.isEmpty()) {
            tags += (" " + tag2);
        }

        String encodedTags = URLEncoder.encode(tags, StandardCharsets.UTF_8);
        String url = String.format(BASE_URL, encodedTags, getLogin(), getApiKey());
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        LOGGER.log(Level.INFO, "Issuing request to Danbooru for tags: {0}", tags);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.log(Level.INFO, "Obtaining response.");
        if (response.statusCode() != 200) {
            LOGGER.log(Level.ERROR, "Failed to execute HTTP request! Status code: {0}, Response: {1}", response.statusCode(), response.body());
            throw new IOException("Failed to execute HTTP request");
        }
        LOGGER.log(Level.INFO, "Successfully obtained response.");
        String responseBody = response.body();
        
        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            return jsonReader.readArray();
        }
    }
}
