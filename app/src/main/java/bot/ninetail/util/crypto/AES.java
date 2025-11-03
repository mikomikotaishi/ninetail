package bot.ninetail.util.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.experimental.UtilityClass;

/**
 * AES encryption and decryption utility.
 */
@UtilityClass
public final class AES {
    /**
     * ThreadLocal to store the cipher instance.
     */
    private static final ThreadLocal<Cipher> cipher = ThreadLocal.withInitial(() -> {
        try {
            return Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    });

    /**
     * ThreadLocal to store the key generator instance.
     */
    private static final ThreadLocal<KeyGenerator> keyGen = ThreadLocal.withInitial(() -> {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            return keyGen;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

    /**
     * Generates a new AES key.
     *
     * @return A new AES key.
     */
    private static SecretKey generateKey() {
        return keyGen.get().generateKey();
    }

    /**
     * Encrypts a plaintext string using the provided secret key.
     *
     * @param plaintext The plaintext to encrypt.
     * @param secretKey The secret key to use for encryption.
     * 
     * @return The encrypted ciphertext.
     * 
     * @throws BadPaddingException If the padding is bad.
     * @throws IllegalBlockSizeException If the block size is illegal.
     * @throws InvalidKeyException If the key is invalid.
     */
    public static String encrypt(String plaintext, SecretKey secretKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipherInstance = cipher.get();
        cipherInstance.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipherInstance.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts a ciphertext string using the provided secret key.
     *
     * @param ciphertext The ciphertext to decrypt.
     * @param secretKey The secret key to use for decryption.
     * 
     * @return The decrypted plaintext.
     * 
     * @throws BadPaddingException If the padding is bad.
     * @throws IllegalBlockSizeException If the block size is illegal.
     * @throws InvalidKeyException If the key is invalid.
     */
    public static String decrypt(String ciphertext, SecretKey secretKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipherInstance = cipher.get();
        cipherInstance.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipherInstance.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes);
    }

    /**
     * Generates a new AES key and returns it as a string.
     *
     * @return The new AES key as a string.
     */
    public static SecretKey getKeyFromString(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * Converts an AES key to a string.
     *
     * @param secretKey The secret key to convert.
     * 
     * @return The secret key as a string.
     */
    public static String keyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}
