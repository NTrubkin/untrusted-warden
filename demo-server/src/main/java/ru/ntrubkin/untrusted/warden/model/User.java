package ru.ntrubkin.untrusted.warden.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class User {
    private UUID id;
    private String username;
    private String password;
}
