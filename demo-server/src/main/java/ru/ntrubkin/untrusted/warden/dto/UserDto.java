package ru.ntrubkin.untrusted.warden.dto;

import lombok.Builder;

@Builder
public record UserDto(
    String username,
    byte[] publicKey
) {
}
