package bot.ninetail.clients;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.*;

import bot.ninetail.structures.clients.LocationInformationClient;
import bot.ninetail.system.ConfigLoader;

import jakarta.annotation.Nonnull;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

/**
 * Client for OpenWeatherMap.
 * 
 * @extends LocationInformationClient
 */
public class WeatherClient extends LocationInformationClient {
    /**
     * The base URL for OpenWeatherMap.
     */
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    /**
     * Constructs a new OpenWeatherMap client.
     */
    public WeatherClient() {
        super(BASE_URL, ConfigLoader.getWeatherToken());
    }

    /**
     * Retrieves the weather from OpenWeatherMap.
     *
     * @param location The location.
     * @return The weather.
     * @throws IOException If an error occurs while retrieving the weather.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public JsonObject getInfo(@Nonnull String location) throws IOException, InterruptedException {
        if (getApiKey() == null || getApiKey().isEmpty()) {
            System.err.println("Weather API key missing!");
            throw new IllegalArgumentException("No Weather token found!");
        }

        String url = String.format(BASE_URL, location, getApiKey());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        System.out.println("Issuing request to OpenWeatherMap for location: " + location);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Obtaining response.");
        if (response.statusCode() != 200) {
            System.err.println("Failed to execute HTTP request!");
            throw new IOException("Failed to execute HTTP request: " + response.statusCode());
        }
        System.out.println("Successfully obtained response.");
        String responseBody = response.body();
        
        try (JsonReader jsonReader = Json.createReader(new StringReader(responseBody))) {
            JsonObject jsonResponse = jsonReader.readObject();
            return jsonResponse.getJsonObject("main");
        }
    }
}
