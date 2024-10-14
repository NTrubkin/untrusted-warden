package ru.ntrubkin.untrusted.warden.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record CurrentUserDto(
    String username,
    byte[] publicKey,
    byte[] encryptedPrivateKey,
    Map<String, byte[]> inbox
) {
}
