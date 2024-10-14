package ru.ntrubkin.untrusted.warden.component;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SymmetricEncryptorTest {

    SymmetricEncryptor encryptor = new SymmetricEncryptor();

    @Test
    void encryptDecrypt() {
        // given
        String plainText = "test-text";
        String password = "test-password";

        // when
        byte[] encrypted = encryptor.encrypt(plainText.getBytes(), password);
        byte[] decrypted = encryptor.decrypt(encrypted, password);

        // then
        var decryptedText = new String(decrypted);
        assertThat(decryptedText).isEqualTo(plainText);
    }
}