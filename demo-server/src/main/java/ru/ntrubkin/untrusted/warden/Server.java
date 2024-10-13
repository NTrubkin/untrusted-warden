package ru.ntrubkin.untrusted.warden;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.ntrubkin.untrusted.warden.dto.AuthDto;
import ru.ntrubkin.untrusted.warden.dto.GroupDto;
import ru.ntrubkin.untrusted.warden.dto.CurrentUserDto;
import ru.ntrubkin.untrusted.warden.dto.UserDto;
import ru.ntrubkin.untrusted.warden.model.Group;
import ru.ntrubkin.untrusted.warden.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.UUID.randomUUID;

@RequiredArgsConstructor
public class Server {

    private final List<User> users = new ArrayList<>();
    private final List<Group> groups = new ArrayList<>();
    private final ObjectMapper objectMapper;

    public void registerUser(String username, String password, String publicKey, String encryptedPrivateKey) {
        boolean userExists = users.stream()
            .anyMatch(user -> user.getUsername().equals(username));
        if (userExists) {
            throw new RuntimeException("Юзер с таким юзернеймом уже существует");
        }
        User user = User.builder()
            .id(randomUUID())
            .username(username)
            .password(password)
            .publicKey(publicKey)
            .encryptedPrivateKey(encryptedPrivateKey)
            .build();
        users.add(user);
    }

    public void login(AuthDto auth) {
        users.stream()
            .filter(user -> user.getUsername().equals(auth.username()))
            .filter(user -> user.getPassword().equals(auth.password()))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Неверный пароль"));
    }

    public List<UserDto> getUsers(AuthDto auth) {
        login(auth);
        return users.stream()
            .map(user -> new UserDto(user.getUsername()))
            .toList();
    }

    public void createGroup(String groupName, AuthDto auth) {
        login(auth);
        boolean groupExists = groups.stream()
            .anyMatch(group -> group.getName().equals(groupName));
        if (groupExists) {
            throw new RuntimeException("Группа с таким названием уже существует");
        }
        User user = findUser(auth.username());
        Group group = Group.builder()
            .name(groupName)
            .members(new ArrayList<>())
            .passwords(new HashMap<>())
            .build();
        group.getMembers().add(user);
        groups.add(group);
    }

    public List<GroupDto> getMyGroups(AuthDto auth) {
        login(auth);
        User user = findUser(auth.username());
        return groups.stream()
            .filter(group -> group.getMembers().contains(user))
            .map(this::toDto)
            .toList();
    }

    private GroupDto toDto(Group group) {
        return new GroupDto(
            group.getName(),
            group.getMembers().stream().map(User::getUsername).toList(),
            group.getPasswords()
        );
    }

    public void addUserToGroup(String username, String groupName, AuthDto auth) {
        login(auth);
        Group group = findGroup(groupName);
        checkAuthUserIsMember(auth, group);
        User newUser = findUser(username);
        boolean userIsMember = group.getMembers().contains(newUser);
        if (userIsMember) {
            return;
        }
        group.getMembers().add(newUser);
    }

    private void checkAuthUserIsMember(AuthDto auth, Group group) {
        User user = findUser(auth.username());
        if (!group.getMembers().contains(user)) {
            throw new RuntimeException("Юзер не в группе");
        }
    }

    private Group findGroup(String groupName) {
        return groups.stream()
            .filter(group -> group.getName().equals(groupName))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Группа не найдена"));
    }

    private User findUser(String username) {
        return users.stream()
            .filter(user -> user.getUsername().equals(username))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Юзер не найден"));
    }

    public void removeUserFromGroup(String username, String groupName, AuthDto auth) {
        login(auth);
        Group group = findGroup(groupName);
        checkAuthUserIsMember(auth, group);
        User user = findUser(username);
        group.getMembers().remove(user);
    }

    public void addPasswordToGroup(String passwordName, String password, String groupName, AuthDto auth) {
        login(auth);
        Group group = findGroup(groupName);
        checkAuthUserIsMember(auth, group);
        group.getPasswords().put(passwordName, password);
    }

    public void removePasswordFromGroup(String passwordName, String groupName, AuthDto auth) {
        login(auth);
        Group group = findGroup(groupName);
        checkAuthUserIsMember(auth, group);
        group.getPasswords().remove(passwordName);
    }

    @SneakyThrows
    public GroupDto getGroup(String groupName, AuthDto auth) {
        login(auth);
        Group group = findGroup(groupName);
        checkAuthUserIsMember(auth, group);
        return toDto(group);
    }

    public CurrentUserDto getCurrentUser(AuthDto auth) {
        login(auth);
        User user = findUser(auth.username());
        return new CurrentUserDto(user.getUsername(), user.getPublicKey(), user.getEncryptedPrivateKey());
    }
}
