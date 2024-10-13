package ru.ntrubkin.untrusted.warden.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class Group {
    String name;
    List<User> members;
    Map<String, String> passwords;
}
