package ru.ntrubkin.untrusted.warden.component;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.assertj.core.api.Assertions.assertThat;

class AsymmetricEncryptorTest {

    AsymmetricEncryptor encryptor = new AsymmetricEncryptor();

    @Test
    void encryptDecrypt() {
        // given
        String plainText = "test-text";
        KeyPair keyPair = encryptor.generateKeyPair();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();

        // when
        byte[] encrypted = encryptor.encrypt(plainText.getBytes(), publicKey);
        byte[] decrypted = encryptor.decrypt(encrypted, privateKey);

        // then
        var decryptedText = new String(decrypted);
        assertThat(decryptedText).isEqualTo(plainText);
    }
}