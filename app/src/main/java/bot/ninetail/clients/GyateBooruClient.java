package bot.ninetail.clients;

import java.io.IOException;
import java.io.StringReader;
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
 * Client for Gyate Booru.
 * 
 * @extends ImageboardClient
 */
public class GyateBooruClient extends ImageboardClient {
    /**
     * The base URL for Gyate Booru.
     */
    private static final String BASE_URL = "https://gyate.net/posts.json?tags=%s&api_key=%s";

    /**
     * Constructs a new Gyate Booru client.
     */
    public GyateBooruClient() {
        super(BASE_URL, ConfigLoader.getGyateBooruToken());
    }

    /**
     * Retrieves posts from Gyate Booru.
     *
     * @param tag1 The first tag.
     * @param tag2 The second tag.
     * @return The posts.
     * @throws IOException If an error occurs while retrieving the posts.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public JsonArray getPosts(@Nonnull String tag1, String tag2) throws IOException, InterruptedException {
        if (getApiKey() == null || getApiKey().isEmpty()) {
            System.err.println("Gyate Booru API key missing!");
            throw new IllegalArgumentException("No Gyate Booru token found!");
        }

        String tags = tag1;
        if (tag2 != null && !tag2.isEmpty())
            tags += ("+" + tag2);
        
        String url = String.format(BASE_URL, tags, getApiKey());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        System.out.println("Issuing request to Gyate Booru for tags: " + tags);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Obtaining response.");
        if (response.statusCode() != 200) {
            System.err.println("Failed to execute HTTP request!");
            throw new IOException("Failed to execute HTTP request");
        }
        System.out.println("Successfully obtained response.");
        String responseBody = response.body();
        
        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            JsonObject jsonResponse = jsonReader.readObject();
            if (!jsonResponse.containsKey("post")) {
                System.err.println("No 'post' key in Gyate Booru response");
                return Json.createArrayBuilder().build();
            }
            return jsonResponse.getJsonArray("post");
        }
    }
}
