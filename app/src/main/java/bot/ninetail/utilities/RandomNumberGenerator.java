package bot.ninetail.utilities;

import java.util.concurrent.ThreadLocalRandom;

import bot.ninetail.structures.Manager;

import lombok.experimental.UtilityClass;

/**
 * Utility class for generating random numbers.
 * 
 * @extends Manager
 */
@UtilityClass
public class RandomNumberGenerator extends Manager {
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
