package bot.ninetail.utilities;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating random numbers.
 */
public class RandomNumberGenerator {
    /**
     * Private constructor to prevent instantiation.
     */
    private RandomNumberGenerator() {}

    /**
     * Generates a random number.
     *
     * @param n The upper bound of the random number.
     * @return The random number.
     */
    public static int generateRandomNumber(int n) {
        return ThreadLocalRandom.current().nextInt(n);
    }
}
