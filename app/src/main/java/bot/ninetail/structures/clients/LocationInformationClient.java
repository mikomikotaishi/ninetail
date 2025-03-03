package bot.ninetail.structures.clients;

import java.io.IOException;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonObject;

import bot.ninetail.structures.Client;

/**
 * Abstract class for location information clients.
 * This abstract class provides a base structure for grabbing location information from services.
 * 
 * @extends Client
 */
public abstract class LocationInformationClient extends Client {
    /**
     * Constructs a new ImageboardClient.
     *
     * @param baseUrl The base URL of the client.
     * @param apiKey The API key of the client.
     */
    protected LocationInformationClient(@Nonnull String baseUrl, @Nonnull String apiKey) {
        super(baseUrl, apiKey);
    }

    /**
     * Retrieves information from a location.
     *
     * @param location The location.
     * @return The information.
     * @throws IOException If an error occurs while retrieving the information.
     * @throws InterruptedException If the operation is interrupted.
     */
    public JsonObject getInfo(@Nonnull String location) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("This method needs to be implemented by subclasses");
    }
}
