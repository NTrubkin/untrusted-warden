package ru.ntrubkin.untrusted.warden.dto;

public record AuthDto(
    String username,
    String password
) {
}
