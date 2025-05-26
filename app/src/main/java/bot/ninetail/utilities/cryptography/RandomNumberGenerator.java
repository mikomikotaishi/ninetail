package bot.ninetail.utilities.cryptography;

import java.security.SecureRandom;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.Manager;

import lombok.experimental.UtilityClass;

/**
 * Random number generator.
 * 
 * @extends Manager
 */
@UtilityClass
public class RandomNumberGenerator extends Manager {
    /**
     * Thread-local random number generator.
     */
    @Nonnull
    private static final ThreadLocal<SecureRandom> rng = ThreadLocal.withInitial(SecureRandom::new);

    /**
     * Generates a random number.
     *
     * @param n The upper bound of the random number.
     * 
     * @return The random number.
     */
    public static int generateRandomNumber(int n) {
        return rng.get().nextInt(n);
    }

    /**
     * Generates random bytes.
     *
     * @param numBytes The number of bytes to generate.
     * 
     * @return The random bytes.
     */
    public static byte[] generateRandomBytes(int numBytes) {
        byte[] bytes = new byte[numBytes];
        rng.get().nextBytes(bytes);
        return bytes;
    }
}
