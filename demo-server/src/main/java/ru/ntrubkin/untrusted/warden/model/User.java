package ru.ntrubkin.untrusted.warden.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record User(
    UUID id,
    String username,
    String password
) {
}
