package ru.ntrubkin.untrusted.warden.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateUserResponse(
    UUID id
) {
}
