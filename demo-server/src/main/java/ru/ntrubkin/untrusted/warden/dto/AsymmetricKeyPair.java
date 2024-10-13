package ru.ntrubkin.untrusted.warden.dto;

public record AsymmetricKeyPair(
    String publicKey,
    String privateKey
) {
}
