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
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import bot.ninetail.structures.clients.ImageboardClient;
import bot.ninetail.system.ConfigLoader;

/**
 * Client for Gelbooru.
 * 
 * @extends ImageboardClient
 */
public class GelbooruClient extends ImageboardClient {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(GelbooruClient.class.getName());

    /**
     * The base URL for Gelbooru.
     */
    @Nonnull
    private static final String BASE_URL = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&tags=%s&user_id=%s&api_key=%s";

    /**
     * Constructs a new Gelbooru client.
     */
    public GelbooruClient() {
        super(BASE_URL, ConfigLoader.getGelbooruLogin(), ConfigLoader.getGelbooruToken(), 10, 1000);
    }
    
    /**
     * Retrieves posts from Gelbooru.
     *
     * @param tags The tags to search for.
     * 
     * @return The posts.
     * 
     * @throws IOException If an error occurs while retrieving the posts.
     * @throws InterruptedException If the operation is interrupted.
     */
    public JsonArray getPosts(@Nonnull String... tags) throws IOException, InterruptedException {
        if (getApiKey() == null || getApiKey().isEmpty()) {
            LOGGER.log(Level.ERROR, "Gelbooru API key missing!");
            throw new IllegalArgumentException("No Gelbooru token found!");
        }

        if (getLogin() == null || getLogin().isEmpty()) {
            LOGGER.log(Level.ERROR, "Gelbooru user ID missing!");
            throw new IllegalArgumentException("No Gelbooru user ID found!");
        }

        waitForRateLimit();

        StringBuilder tagsBuilder = new StringBuilder();
        for (int i = 0; i < tags.length; ++i) {
            if (tags[i] != null && !tags[i].isEmpty()) {
                if (tagsBuilder.length() > 0) {
                    tagsBuilder.append(" ");
                }
                tagsBuilder.append(tags[i]);
            }
        }

        String combinedTags = tagsBuilder.toString();
        if (combinedTags.isEmpty()) {
            throw new IllegalArgumentException("At least one tag must be provided");
        }

        String encodedTags = URLEncoder.encode(combinedTags, StandardCharsets.UTF_8);
        String url = String.format(BASE_URL, encodedTags, getLogin(), getApiKey());
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        LOGGER.log(Level.INFO, "Issuing request to Gelbooru for tags: {0}", combinedTags);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.log(Level.INFO, "Obtaining response.");
        if (response.statusCode() != 200) {
            LOGGER.log(Level.ERROR, "Failed to execute HTTP request! Status code: {0}, Response: {1}", 
                response.statusCode(), response.body());
            throw new IOException("Failed to execute HTTP request");
        }
        LOGGER.log(Level.INFO, "Successfully obtained response.");
        String responseBody = response.body();
        
        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            JsonObject jsonResponse = jsonReader.readObject();

            if (jsonResponse.containsKey("@attributes")) {
                JsonObject attributes = jsonResponse.getJsonObject("@attributes");
                if (attributes.containsKey("count") && attributes.getInt("count") == 0) {
                    LOGGER.log(Level.INFO, "No posts found for tags");
                    return Json.createArrayBuilder().build();
                }
            }

            if (!jsonResponse.containsKey("post")) {
                LOGGER.log(Level.ERROR, "No 'post' key in Gelbooru response");
                return Json.createArrayBuilder().build();
            }
            return jsonResponse.getJsonArray("post");
        }
    }
}
