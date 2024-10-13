package ru.ntrubkin.untrusted.warden.dto;

public record CurrentUserDto(
    String username,
    String publicKey,
    String encryptedPrivateKey
) {
}
