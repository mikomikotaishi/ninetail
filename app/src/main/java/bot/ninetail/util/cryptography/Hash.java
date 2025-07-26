package bot.ninetail.util.cryptography;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import lombok.experimental.UtilityClass;

/**
 * Hashing utility.
 */
@UtilityClass
public final class Hash {
    /**
     * Hashes a message.
     *
     * @param message The message to hash.
     * @param algorithmName The name of the algorithm to use.
     * 
     * @return The hash.
     * 
     * @throws NoSuchAlgorithmException If the algorithm is not found.
     */
    public static String hash(String message, String algorithmName) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithmName);
        byte[] hashBytes = digest.digest(message.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    /**
     * Verifies a hash.
     *
     * @param message The message to verify.
     * @param hash The hash to verify.
     * @param algorithmName The name of the algorithm to use.
     * 
     * @return True if the hash is verified, false otherwise.
     * 
     * @throws NoSuchAlgorithmException If the algorithm is not found.
     */
    public static boolean verifyHash(String message, String hash, String algorithmName) throws NoSuchAlgorithmException {
        String newHash = hash(message, algorithmName);
        return newHash.equals(hash);
    }
}
