package ru.ntrubkin.untrusted.warden.component;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.security.MessageDigest;

@RequiredArgsConstructor
public class BasePasswordHasher implements Hasher {

    private final String algorithm;

    @Override
    @SneakyThrows
    public String hash(String source) {
        var digest = MessageDigest.getInstance(algorithm);
        byte[] hash = digest.digest(source.getBytes());
        StringBuilder builder = new StringBuilder();
        for (byte hashByte : hash) {
            builder.append(String.format("%02x", hashByte));
        }
        return builder.toString();
    }
}
