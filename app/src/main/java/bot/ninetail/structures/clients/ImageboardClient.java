package bot.ninetail.structures.clients;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;

import bot.ninetail.structures.Client;

/**
 * Abstract class for imageboard clients.
 * This abstract class provides a base structure for grabbing posts from imageboards.
 * 
 * @extends Client
 */
public abstract class ImageboardClient extends Client {
    /**
     * The login username or user ID of the client.
     */
    @Nonnull 
    private final String LOGIN;

    /**
     * Maximum number of requests allowed per time period.
     */
    private final int requestsPerPeriod;

    /**
     * Time period duration in milliseconds.
     */
    private final long periodDurationMs;

    /**
     * Queue to track timestamps of recent requests for rate limiting.
     */
    private final Deque<Long> requestTimestamps = new LinkedList<>();

    /**
     * Constructs a new ImageboardClient.
     *
     * @param baseUrl The base URL of the client.
     * @param login The login username or user ID.
     * @param apiKey The API key of the client.
     * @param requestsPerPeriod Maximum number of requests allowed per time period.
     * @param periodDurationMs Time period duration in milliseconds.
     */
    protected ImageboardClient(@Nonnull String baseUrl, @Nonnull String login, @Nonnull String apiKey, 
                               int requestsPerPeriod, long periodDurationMs) {
        super(baseUrl, apiKey);
        this.LOGIN = login;
        this.requestsPerPeriod = requestsPerPeriod;
        this.periodDurationMs = periodDurationMs;
    }

    /**
     * Gets the login username or user ID of the client.
     *
     * @return The login username or user ID.
     */
    public String getLogin() {
        return LOGIN;
    }

    /**
     * Enforces rate limiting by waiting if necessary before allowing a request.
     * This method should be called before making any API request.
     * 
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    protected synchronized void waitForRateLimit() throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        
        // Remove timestamps older than the period duration
        while (!requestTimestamps.isEmpty() && 
               currentTime - requestTimestamps.peekFirst() >= periodDurationMs) {
            requestTimestamps.pollFirst();
        }
        
        // If we've hit the rate limit, wait until the oldest request expires
        if (requestTimestamps.size() >= requestsPerPeriod) {
            long oldestRequestTime = requestTimestamps.peekFirst();
            long waitTime = periodDurationMs - (currentTime - oldestRequestTime);
            if (waitTime > 0) {
                Thread.sleep(waitTime);
                // Re-check and clean up after waiting
                currentTime = System.currentTimeMillis();
                while (!requestTimestamps.isEmpty() && 
                       currentTime - requestTimestamps.peekFirst() >= periodDurationMs) {
                    requestTimestamps.pollFirst();
                }
            }
        }
        
        // Record this request
        requestTimestamps.addLast(System.currentTimeMillis());
    }

    /**
     * Gets posts from the imageboard.
     * 
     * @param tag1 The first tag to search for.
     * @param tag2 The second tag to search for (optional).
     * 
     * @return A JsonArray containing the posts.
     * 
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    public JsonArray getPosts(@Nonnull String tag1, String tag2) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("This method needs to be implemented by subclasses");
    }
}
