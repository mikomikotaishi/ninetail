package bot.ninetail.clients;

import java.io.IOException;
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
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
    private static final Logger LOGGER = System.getLogger(RandomFoxClient.class.getName());

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
