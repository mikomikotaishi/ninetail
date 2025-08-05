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

import bot.ninetail.structures.clients.RandomImageClient;

/**
 * Client for retrieving a random fox image.
 * 
 * @extends RandomImageClient
 */
public class RandomFoxClient extends RandomImageClient {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(RandomFoxClient.class.getName());

    /**
     * The base URL for the RandomFox API.
     */
    @Nonnull
    private static final String BASE_URL = "https://randomfox.ca/floof/";

    /**
     * Constructs a new RandomFoxClient.
     */
    public RandomFoxClient() {
        super(BASE_URL, "placeholder");
    }

    /**
     * Retrieves a random image of a fox.
     *
     * @return A JsonArray containing the API response.
     * 
     * @throws IOException If an error occurs while retrieving the image.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public JsonArray getImage() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.log(Level.INFO, "Obtaining response.");

        if (response.statusCode() != 200) {
            LOGGER.log(Level.ERROR, "Failed to execute HTTP request!");
            throw new IOException("Unexpected response code: " + response.statusCode());
        }
        LOGGER.log(Level.INFO, "Successfully obtained response.");
        String responseBody = response.body();

        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            JsonObject jsonObject = jsonReader.readObject();
            JsonArray array = Json.createArrayBuilder().add(jsonObject).build();
            return array;
        }
    }
}
