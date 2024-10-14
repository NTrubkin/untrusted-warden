package ru.ntrubkin.untrusted.warden.component;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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
    public byte[] encrypt(byte[] plainText, String password) {
        byte[] salt = generateSalt();
        byte[] iv = generateIV();
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKey key = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] cipherText = cipher.doFinal(plainText);

        byte[] encryptedMessage = new byte[salt.length + iv.length + cipherText.length];
        System.arraycopy(salt, 0, encryptedMessage, 0, salt.length);
        System.arraycopy(iv, 0, encryptedMessage, salt.length, iv.length);
        System.arraycopy(cipherText, 0, encryptedMessage, salt.length + iv.length, cipherText.length);

        return encryptedMessage;
    }

    @SneakyThrows
    public byte[] decrypt(byte[] encryptedText, String password) {
        byte[] salt = new byte[SALT_SIZE];
        byte[] iv = new byte[IV_SIZE];
        byte[] cipherText = new byte[encryptedText.length - SALT_SIZE - IV_SIZE];
        System.arraycopy(encryptedText, 0, salt, 0, SALT_SIZE);
        System.arraycopy(encryptedText, SALT_SIZE, iv, 0, IV_SIZE);
        System.arraycopy(encryptedText, SALT_SIZE + IV_SIZE, cipherText, 0, cipherText.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKey key = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        return cipher.doFinal(cipherText);
    }


    public static void main(String[] args) {
        SymmetricEncryptor symmetricEncryptor = new SymmetricEncryptor();
        byte[] encrypted = symmetricEncryptor.encrypt("hw".getBytes(), "password1");
        byte[] decrypted = symmetricEncryptor.decrypt(encrypted, "password1");
        System.out.println(new String(decrypted));
    }
}
