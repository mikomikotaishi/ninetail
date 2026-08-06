package bot.ninetail.clients;

import java.io.IOException;
import java.io.StringReader;
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
 * Client for Gyate Booru.
 * 
 * @extends ImageboardClient
 */
public class GyateBooruClient extends ImageboardClient {
    @Nonnull
    private static final System.Logger LOGGER = System.getLogger(GyateBooruClient.class.getName());

    /**
     * The base URL for Gyate Booru.
     */
    @Nonnull
    private static final String BASE_URL = "https://gyate.net/posts.json?tags=%s&api_key=%s";

    /**
     * Constructs a new Gyate Booru client.
     */
    public GyateBooruClient() {
        super(BASE_URL, "", ConfigLoader.getGyateBooruToken(), 1, 1000);
    }

    /**
     * Retrieves posts from Gyate Booru.
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
            LOGGER.log(System.Logger.Level.ERROR, "Gyate Booru API key missing!");
            throw new IllegalArgumentException("No Gyate Booru token found!");
        }

        waitForRateLimit();

        String tags = tag1;
        if (tag2 != null && !tag2.isEmpty()) {
            tags += ("+" + tag2);
        }

        String encodedTags = URLEncoder.encode(tags, StandardCharsets.UTF_8);
        String url = String.format(BASE_URL, encodedTags, getApiKey());
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        LOGGER.log(System.Logger.Level.INFO, "Issuing request to Gyate Booru for tags: {0}", tags);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.log(System.Logger.Level.INFO, "Obtaining response.");
        if (response.statusCode() != 200) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to execute HTTP request!");
            throw new IOException("Failed to execute HTTP request");
        }
        LOGGER.log(System.Logger.Level.INFO, "Successfully obtained response.");
        String responseBody = response.body();

        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            JsonObject jsonResponse = jsonReader.readObject();
            if (!jsonResponse.containsKey("post")) {
                LOGGER.log(System.Logger.Level.ERROR, "No 'post' key in Gyate Booru response");
                return Json.createArrayBuilder().build();
            }
            return jsonResponse.getJsonArray("post");
        }
    }
}
