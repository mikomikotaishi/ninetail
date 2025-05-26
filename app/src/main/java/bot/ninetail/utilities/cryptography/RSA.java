package bot.ninetail.utilities.cryptography;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.experimental.UtilityClass;

/**
 * RSA encryption utility.
 */
@UtilityClass
public class RSA {
    /**
     * Thread-local cipher.
     */
    private static final ThreadLocal<Cipher> cipher = ThreadLocal.withInitial(() -> {
        try {
            return Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    });

    /**
     * Thread-local key pair generator.
     */
    private static final ThreadLocal<KeyPair> keyGen = ThreadLocal.withInitial(() -> {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

    /**
     * Generates a key pair.
     * 
     * @param plaintext The plaintext to encrypt.
     * @param publicKey The public key.
     * 
     * @return The encrypted text.
     * 
     * @throws BadPaddingException If padding is bad.
     * @throws IllegalBlockSizeException If the block size is illegal.
     * @throws InvalidKeyException If the key is invalid.
     */
    public static String encrypt(String plaintext, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipherInstance = cipher.get();
        cipherInstance.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipherInstance.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts a ciphertext.
     * 
     * @param ciphertext The ciphertext to decrypt.
     * @param privateKey The private key.
     * 
     * @return The decrypted text.
     * 
     * @throws BadPaddingException If padding is bad.
     * @throws IllegalBlockSizeException If the block size is illegal.
     * @throws InvalidKeyException If the key is invalid.
     */
    public static String decrypt(String ciphertext, PrivateKey privateKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipherInstance = cipher.get();
        cipherInstance.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipherInstance.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes);
    }

    /**
     * Generates a key pair.
     * 
     * @return The key pair.
     */
    public static String keyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Generates a key pair.
     * 
     * @return The key pair.
     */
    public static String keyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * Generates a key pair.
     * 
     * @param keyString The key string.
     * 
     * @return The public key.
     * 
     * @throws InvalidKeySpecException If the key spec is invalid.
     * @throws NoSuchAlgorithmException If the algorithm is not found.
     */
    public static PublicKey getPublicKeyFromString(String keyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedKey));
    }

    /**
     * Generates a key pair.
     * 
     * @param keyString The key string.
     * 
     * @return The private key.
     * 
     * @throws InvalidKeySpecException If the key spec is invalid.
     * @throws NoSuchAlgorithmException If the algorithm is not found.
     */
    public static PrivateKey getPrivateKeyFromString(String keyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(decodedKey));
    }
}
