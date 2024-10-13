package ru.ntrubkin.untrusted.warden.dto;

import java.util.List;
import java.util.Map;

public record GroupDto(
    String name,
    List<String> members,
    Map<String, String> passwords
) {
}
