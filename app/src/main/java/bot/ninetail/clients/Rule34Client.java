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
 * Client for Rule34.
 * 
 * @extends ImageboardClient
 */
public class Rule34Client extends ImageboardClient {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Rule34Client.class.getName());

    /**
     * The base URL for Rule34.
     */
    @Nonnull
    private static final String BASE_URL = "https://rule34.xxx/index.php?page=dapi&s=post&q=index&json=1&tags=%s&api_key=%s";

    /**
     * Constructs a new Rule34 client.
     */
    public Rule34Client() {
        super(BASE_URL, ConfigLoader.getRule34Token());
    }

    /**
     * Retrieves posts from Rule34.
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
            LOGGER.log(Level.ERROR, "Rule34 API key missing!");
            throw new IllegalArgumentException("No Rule34 token found!");
        }

        String tags = tag1;
        if (tag2 != null && !tag2.isEmpty()) {
            tags += ("+" + tag2);
        }
        
        String url = String.format(BASE_URL, tags, getApiKey());
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        LOGGER.log(Level.INFO, "Issuing request to Rule34 for tags: {0}", tags);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.log(Level.INFO, "Obtaining response.");
        if (response.statusCode() != 200) {
            LOGGER.log(Level.ERROR, "Failed to execute HTTP request! Status: {0}", response.statusCode());
            throw new IOException("Failed to execute HTTP request: " + response.statusCode());
        }
        LOGGER.log(Level.INFO, "Successfully obtained response.");
        String responseBody = response.body();
        
        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            JsonObject jsonResponse = jsonReader.readObject();
            if (!jsonResponse.containsKey("post")) {
                LOGGER.log(Level.ERROR, "No 'post' key in Rule34 response");
                return Json.createArrayBuilder().build();
            }
            return jsonResponse.getJsonArray("post");
        }
    }
}
