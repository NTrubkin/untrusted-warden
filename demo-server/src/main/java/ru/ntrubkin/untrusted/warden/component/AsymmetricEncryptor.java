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

public class AsymmetricEncryptor {

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;  // You can also use 4096 for stronger security


    @SneakyThrows
    public KeyPair generateKeyPair() {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom random = new SecureRandom();
        keyGen.initialize(KEY_SIZE, random);
        return keyGen.generateKeyPair();
    }

    public byte[] encrypt(byte[] source, byte[] publicKey) {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    public byte[] decrypt(byte[] source, byte[] privateKey) {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }
}
