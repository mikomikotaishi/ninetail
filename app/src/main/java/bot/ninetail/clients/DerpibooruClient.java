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
 * Client for Derpibooru.
 * 
 * @extends ImageboardClient
 */
public class DerpibooruClient extends ImageboardClient {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(DerpibooruClient.class.getName());

    /**
     * The base URL for Derpibooru API.
     */
    @Nonnull
    private static final String BASE_URL = "https://derpibooru.org/api/v1/json/search/images?q=%s&key=%s";

    /**
     * Constructs a new Derpibooru client.
     */
    public DerpibooruClient() {
        super(BASE_URL, ConfigLoader.getDerpibooruLogin(), ConfigLoader.getDerpibooruToken(), 20, 10000);
    }

    /**
     * Retrieves posts from Derpibooru.
     *
     * @param tags The tags to search for.
     * 
     * @return The posts (images).
     * 
     * @throws IOException If an error occurs while retrieving the posts.
     * @throws InterruptedException If the operation is interrupted.
     */
    public JsonArray getPosts(@Nonnull String... tags) throws IOException, InterruptedException {
        if (getApiKey() == null || getApiKey().isEmpty()) {
            LOGGER.log(Level.ERROR, "Derpibooru API key missing!");
            throw new IllegalArgumentException("No Derpibooru token found!");
        }

        waitForRateLimit();

        StringBuilder tagsBuilder = new StringBuilder();
        for (int i = 0; i < tags.length; ++i) {
            if (tags[i] != null && !tags[i].isEmpty()) {
                if (tagsBuilder.length() > 0) {
                    tagsBuilder.append(", ");
                }
                tagsBuilder.append(tags[i]);
            }
        }

        String combinedTags = tagsBuilder.toString();
        if (combinedTags.isEmpty()) {
            throw new IllegalArgumentException("At least one tag must be provided");
        }

        String encodedTags = URLEncoder.encode(combinedTags, StandardCharsets.UTF_8);
        String url = String.format(BASE_URL, encodedTags, getApiKey());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "NinetailBot/1.0")
            .build();

        LOGGER.log(Level.INFO, "Issuing request to Derpibooru for tags: {0}", combinedTags);
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
            if (!jsonResponse.containsKey("images")) {
                LOGGER.log(Level.ERROR, "No 'images' key in Derpibooru response");
                return Json.createArrayBuilder().build();
            }
            return jsonResponse.getJsonArray("images");
        }
    }
}
