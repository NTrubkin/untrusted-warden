package ru.ntrubkin.untrusted.warden.dto;

public record CreateUserRequest(
    String username,
    String password
) {
}
