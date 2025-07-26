package bot.ninetail.util;

import java.util.concurrent.ThreadLocalRandom;

import lombok.experimental.UtilityClass;

/**
 * Utility class for generating random numbers.
 */
@UtilityClass
public final class RandomNumberGenerator {
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
