package bot.ninetail.clients;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.*;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.clients.AIClient;
import bot.ninetail.system.ConfigLoader;

import com.google.genai.Client;

/**
 * Client for retrieving a random fox image.
 * 
 * @extends RandomImageClient
 */
public class GeminiClient extends AIClient {
    @Nonnull
    private static final System.Logger LOGGER = System.getLogger(GeminiClient.class.getName());

    @Nonnull
    private final Client geminiClient;

    /**
     * Constructs a new GeminiClient.
     */
    public GeminiClient() {
        super(ConfigLoader.getGeminiToken());
        geminiClient = Client.builder()
            .apiKey(getApiKey())
            .build();
    }
}
