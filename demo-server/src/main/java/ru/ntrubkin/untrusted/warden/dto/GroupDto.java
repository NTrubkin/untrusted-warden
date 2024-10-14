package ru.ntrubkin.untrusted.warden.dto;

import java.util.List;

public record GroupDto(
    String name,
    List<String> members,
    List<byte[]> memberPublicKeys,
    byte[] encryptedPasswords
) {
}
