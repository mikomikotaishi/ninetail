package bot.ninetail.structures;

import java.net.http.HttpClient;

import jakarta.annotation.Nonnull;

import lombok.Getter;

/**
 * Abstract class for clients.
 * This class provides a base structure for clients that interact with external services.
 */
@Getter
public abstract class Client {
    /**
     * The base URL of the client.
     */
    @Nonnull private final String BASE_URL;
    /**
     * The API key of the client.
     */
    @Nonnull private final String API_KEY;
    /**
     * The HTTP client.
     */
    protected final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Constructs a new Client.
     *
     * @param baseUrl The base URL of the client.
     * @param apiKey The API key of the client.
     */
    protected Client(@Nonnull String baseUrl, @Nonnull String apiKey) {
        this.BASE_URL = baseUrl;
        this.API_KEY = apiKey;
    }

    /**
     * Gets the base URL of the client.
     *
     * @return The base URL.
     */
    public String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * Gets the API key of the client.
     *
     * @return The API key.
     */
    public String getApiKey() {
        return API_KEY;
    }
}
