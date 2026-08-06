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
    private static final System.Logger LOGGER = System.getLogger(Rule34Client.class.getName());

    /**
     * The base URL for Rule34.
     */
    @Nonnull
    private static final String BASE_URL = "https://rule34.xxx/index.php?page=dapi&s=post&q=index&json=1&tags=%s&user_id=%s&api_key=%s";

    /**
     * Constructs a new Rule34 client.
     */
    public Rule34Client() {
        super(BASE_URL, ConfigLoader.getRule34Login(), ConfigLoader.getRule34Token(), 60, 60000);
    }

    /**
     * Retrieves posts from Rule34.
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
            LOGGER.log(System.Logger.Level.ERROR, "Rule34 API key missing!");
            throw new IllegalArgumentException("No Rule34 token found!");
        }
        
        if (getLogin() == null || getLogin().isEmpty()) {
            LOGGER.log(System.Logger.Level.ERROR, "Rule34 user ID missing!");
            throw new IllegalArgumentException("No Rule34 user ID found!");
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
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
            .header("Accept", "application/json, text/html, application/xhtml+xml, */*")
            .header("Accept-Language", "en-US,en;q=0.9")
            .header("Referer", "https://rule34.xxx/")
            .build();
        LOGGER.log(System.Logger.Level.INFO, "Issuing request to Rule34 for tags: {0}", combinedTags);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.log(System.Logger.Level.INFO, "Obtaining response.");
        if (response.statusCode() != 200) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to execute HTTP request! Status code: {0}, Response: {1}", 
                response.statusCode(), response.body());
            throw new IOException("Failed to execute HTTP request: " + response.statusCode());
        }
        LOGGER.log(System.Logger.Level.INFO, "Successfully obtained response.");
        String responseBody = response.body();
        
        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            return jsonReader.readArray();
        }
    }
}
