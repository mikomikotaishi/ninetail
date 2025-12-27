package bot.ninetail.clients;

import java.io.IOException;
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
    private static final String BASE_URL = "https://e621.net/posts.json?tags=%s&login=%s&api_key=%s";

    /**
     * Constructs a new e621 client.
     */
    public E621Client() {
        super(BASE_URL, ConfigLoader.getE621Login(), ConfigLoader.getE621Token(), 2, 1000);
    }

    /**
     * Retrieves posts from e621.
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
            LOGGER.log(Level.ERROR, "e621 API key missing!");
            throw new IllegalArgumentException("No e621 token found!");
        }

        if (getLogin() == null || getLogin().isEmpty()) {
            LOGGER.log(Level.ERROR, "e621 login missing!");
            throw new IllegalArgumentException("No e621 login found!");
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
            .header("User-Agent", String.format("NinetailBot/1.0 (by %s on e621)", getLogin()))
            .build();
        LOGGER.log(Level.INFO, "Issuing request to e621 for tags: {0}", combinedTags);
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
            if (!jsonResponse.containsKey("posts")) {
                LOGGER.log(Level.ERROR, "No 'posts' key in e621 response");
                return Json.createArrayBuilder().build();
            }
            return jsonResponse.getJsonArray("posts");
        }
    }
}
