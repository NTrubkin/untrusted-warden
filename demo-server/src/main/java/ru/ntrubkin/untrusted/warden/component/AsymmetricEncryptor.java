package ru.ntrubkin.untrusted.warden.component;

import lombok.SneakyThrows;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.Cipher;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class AsymmetricEncryptor {

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    @SneakyThrows
    public KeyPair generateKeyPair() {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom random = new SecureRandom();
        keyGen.initialize(KEY_SIZE, random);
        return keyGen.generateKeyPair();
    }

    @SneakyThrows
    public byte[] encrypt(byte[] plainText, byte[] publicKey) {
        var keyFactory = KeyFactory.getInstance(ALGORITHM);
        var spec = new X509EncodedKeySpec(publicKey);
        PublicKey publicKeyObject = keyFactory.generatePublic(spec);

        Cipher encryptCipher = Cipher.getInstance(ALGORITHM);
        encryptCipher.init(ENCRYPT_MODE, publicKeyObject);
        return encryptCipher.doFinal(plainText);
    }

    @SneakyThrows
    public byte[] decrypt(byte[] cipherText, byte[] privateKey) {
        var keyFactory = KeyFactory.getInstance(ALGORITHM);
        var spec = new PKCS8EncodedKeySpec(privateKey);
        PrivateKey privateKeyObject = keyFactory.generatePrivate(spec);

        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(DECRYPT_MODE, privateKeyObject);
        return decryptCipher.doFinal(cipherText);
    }

    public static void main(String[] args) {
        AsymmetricEncryptor asymmetricEncryptor = new AsymmetricEncryptor();
        KeyPair keyPair = asymmetricEncryptor.generateKeyPair();
        byte[] encrypted = asymmetricEncryptor.encrypt("hw".getBytes(), keyPair.getPublic().getEncoded());
        byte[] decrypted = asymmetricEncryptor.decrypt(encrypted, keyPair.getPrivate().getEncoded());
        System.out.println(new String(decrypted));
    }
}
