package bot.ninetail.structures.clients;

import java.io.IOException;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.Client;

/**
 * Abstract class for text manipulator clients.
 * This abstract class provides a base structure for manipulating text.
 * 
 * @extends Client
 */
public abstract class TextManipulatorClient extends Client {
    /**
     * Constructs a new TextManipulatorClient.
     *
     * @param baseUrl The base URL of the client.
     * @param apiKey The API key of the client.
     */
    protected TextManipulatorClient(@Nonnull String baseUrl, @Nonnull String apiKey) {
        super(baseUrl, apiKey);
    }

    /**
     * Manipulates text.
     *
     * @param text The input text.
     * @return The transformed text.
     * @throws IOException If an error occurs while making the request.
     * @throws InterruptedException If the operation is interrupted.
     */
    public String getText(@Nonnull String text) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("This method needs to be implemented by subclasses");
    }
}
