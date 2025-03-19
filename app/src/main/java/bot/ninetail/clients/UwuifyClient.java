package bot.ninetail.clients;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;

import jakarta.annotation.Nonnull;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.clients.TextManipulatorClient;

/**
 * Client for the Owoify API.
 * 
 * @extends TextManipulatorClient
 */
public class UwuifyClient extends TextManipulatorClient {
    /**
     * The base URL for the Owoify API.
     */
    private static final String BASE_URL = "https://nekos.life/api/v2/owoify?text=%s&api_key=%s";

    /**
     * Constructs a new Uwuify client.
     */
    public UwuifyClient() {
        super(BASE_URL, "placeholder"); // Modify ConfigLoader if needed
    }

    /**
     * Converts a given text to its uwuified version.
     *
     * @param text The input text.
     * @return The transformed text.
     * @throws IOException If an error occurs while making the request.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public String getText(@Nonnull String text) throws IOException, InterruptedException {
        if (getApiKey() == null || getApiKey().isEmpty()) {
            Logger.log(LogLevel.ERROR, "Uwuify API key missing!");
            throw new IllegalArgumentException("No Uwuify token found!");
        }

        String encodedText = URLEncoder.encode(text, java.nio.charset.StandardCharsets.UTF_8);
        String url = String.format(BASE_URL, encodedText, getApiKey());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        
        Logger.log(LogLevel.INFO, "Issuing request to Owoify API for text: " + text);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Logger.log(LogLevel.INFO, "Obtaining response...");

        if (response.statusCode() != 200) {
            Logger.log(LogLevel.ERROR, "Failed to execute HTTP request!");
            throw new IOException("Failed to execute HTTP request: " + response.statusCode());
        }

        Logger.log(LogLevel.INFO, "Successfully obtained response.");
        String responseBody = response.body();
        
        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            JsonObject jsonResponse = jsonReader.readObject();
            return jsonResponse.containsKey("owo") ? jsonResponse.getString("owo") : null;
        }
    }
}
