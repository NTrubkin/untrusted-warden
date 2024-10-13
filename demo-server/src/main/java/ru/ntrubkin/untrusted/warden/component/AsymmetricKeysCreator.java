package ru.ntrubkin.untrusted.warden.component;

import lombok.SneakyThrows;
import ru.ntrubkin.untrusted.warden.dto.AsymmetricKeyPair;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
import java.util.stream.IntStream;

public class AsymmetricKeysCreator {

    // PBKDF2 parameters
    private static final int ITERATIONS = 65536;
    private static final int KEY_SIZE = 2048; // For RSA

    public AsymmetricKeyPair create(String username, String password) {
        // Example password input
        byte[] salt = username.getBytes(); // Randomly generated salt

        // Derive key from password
        byte[] privateKeyBytes = new byte[3];

        // Convert the derived key bytes into an RSA private key
        PrivateKey privateKey = generatePrivateKey(privateKeyBytes);

        // Generate the corresponding public key from the private key
        PublicKey publicKey = generatePublicKey(privateKey);

        // Display the keys in base64
        String priv = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String pub = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return new AsymmetricKeyPair(pub, priv);
    }

    // Method to generate salt (should be random and stored securely)
    @SneakyThrows
    public byte[] generateSalt(){
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // Method to derive key from password
    @SneakyThrows
    public byte[] deriveKeyFromPassword(String password, byte[] salt, int keyLength) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    // Generate RSA PrivateKey from derived key bytes
    @SneakyThrows
    public static PrivateKey generatePrivateKey(byte[] privateKeyBytes) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    // Generate PublicKey from PrivateKey
    @SneakyThrows
    public static PublicKey generatePublicKey(PrivateKey privateKey) {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateKeySpec.getModulus(), BigInteger.valueOf(65537));
        return keyFactory.generatePublic(publicKeySpec);
    }

    @SneakyThrows
    public static void main(String[] args) {
        AsymmetricKeysCreator asymmetricKeysCreator = new AsymmetricKeysCreator();
        System.out.println(asymmetricKeysCreator.create("admin", "admin"));
        System.out.println(asymmetricKeysCreator.create("admin", "admin"));
        System.out.println(asymmetricKeysCreator.create("admin", "admin1"));
    }
}