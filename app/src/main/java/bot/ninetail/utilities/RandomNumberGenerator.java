package bot.ninetail.utilities;

import java.util.concurrent.ThreadLocalRandom;

import bot.ninetail.structures.Manager;

/**
 * Utility class for generating random numbers.
 * 
 * @extends Manager
 */
public class RandomNumberGenerator extends Manager {
    /**
     * Private constructor to prevent instantiation.
     */
    private RandomNumberGenerator() {}

    /**
     * Generates a random number.
     *
     * @param n The upper bound of the random number.
     * 
     * @return The random number.
     */
    public static int generateRandomNumber(int n) {
        return ThreadLocalRandom.current().nextInt(n);
    }
}
