package ru.ntrubkin.untrusted.warden.component;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class SymmetricEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int IV_SIZE = 16;
    private static final int SALT_SIZE = 16;
    private static final int KEY_SIZE = 256;
    private static final int ITERATION_COUNT = 65536;

    private static SecretKey generateKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KDF_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_SIZE];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    @SneakyThrows
    public String encrypt(String plainText, String password) {
        byte[] salt = generateSalt();
        byte[] iv = generateIV();
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKey key = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        byte[] encryptedMessage = new byte[salt.length + iv.length + cipherText.length];
        System.arraycopy(salt, 0, encryptedMessage, 0, salt.length);
        System.arraycopy(iv, 0, encryptedMessage, salt.length, iv.length);
        System.arraycopy(cipherText, 0, encryptedMessage, salt.length + iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    @SneakyThrows
    public String decrypt(String encryptedText, String password) {
        byte[] encryptedMessage = Base64.getDecoder().decode(encryptedText);

        byte[] salt = new byte[SALT_SIZE];
        byte[] iv = new byte[IV_SIZE];
        byte[] cipherText = new byte[encryptedMessage.length - SALT_SIZE - IV_SIZE];
        System.arraycopy(encryptedMessage, 0, salt, 0, SALT_SIZE);
        System.arraycopy(encryptedMessage, SALT_SIZE, iv, 0, IV_SIZE);
        System.arraycopy(encryptedMessage, SALT_SIZE + IV_SIZE, cipherText, 0, cipherText.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKey key = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] plainText = cipher.doFinal(cipherText);

        return new String(plainText);
    }
}
