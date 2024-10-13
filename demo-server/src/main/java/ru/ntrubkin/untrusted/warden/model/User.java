package ru.ntrubkin.untrusted.warden.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class User {
    private UUID id;
    private String username;
    private String password;
    private String encryptedPrivateKey;
    private String publicKey;
    private Map<String, String> inbox = new HashMap<>();
}
