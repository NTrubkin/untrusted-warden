package ru.ntrubkin.untrusted.warden;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.ntrubkin.untrusted.warden.dto.AuthDto;
import ru.ntrubkin.untrusted.warden.dto.CurrentUserDto;
import ru.ntrubkin.untrusted.warden.dto.GroupDto;
import ru.ntrubkin.untrusted.warden.dto.UserDto;
import ru.ntrubkin.untrusted.warden.model.Group;
import ru.ntrubkin.untrusted.warden.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.UUID.randomUUID;

@RequiredArgsConstructor
public class Server {

    private final List<User> users = new ArrayList<>();
    private final List<Group> groups = new ArrayList<>();

    public void registerUser(String username, String password, byte[] publicKey, byte[] encryptedPrivateKey) {
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
            .inbox(new HashMap<>())
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
            .map(this::toDto)
            .toList();
    }

    public void createGroup(String groupName, byte[] encryptedPasswords, byte[] encryptedGroupKey, AuthDto auth) {
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
            .encryptedPasswords(encryptedPasswords)
            .build();
        group.getMembers().add(user);
        user.getInbox().put(group.getName(), encryptedGroupKey);
        groups.add(group);
    }

    private void updateInboxes(String groupName, Map<String, byte[]> inboxUpdates) {
        inboxUpdates.forEach((username, encryptedGroupKey) -> {
            User user = findUser(username);
            user.getInbox().put(groupName, encryptedGroupKey);
        });
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
            group.getMembers().stream().map(User::getPublicKey).toList(),
            group.getEncryptedPasswords()
        );
    }

    public void addUserToGroup(String username, String groupName, byte[] newEncryptedGroupKey, AuthDto auth) {
        login(auth);
        Group group = findGroup(groupName);
        checkAuthUserIsMember(auth, group);
        User newUser = findUser(username);
        boolean userIsMember = group.getMembers().contains(newUser);
        if (userIsMember) {
            return;
        }
        group.getMembers().add(newUser);
        newUser.getInbox().put(group.getName(), newEncryptedGroupKey);
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

    public void removeUserFromGroup(
        String username,
        String groupName,
        byte[] encryptedPasswords,
        Map<String, byte[]> inboxUpdates,
        AuthDto auth
    ) {
        login(auth);
        Group group = findGroup(groupName);
        checkAuthUserIsMember(auth, group);
        User user = findUser(username);
        group.getMembers().remove(user);
        user.getInbox().remove(groupName);
        group.setEncryptedPasswords(encryptedPasswords);
        updateInboxes(groupName, inboxUpdates);
    }

    public void updateGroupPasswords(
        byte[] newEncryptedPassword,
        String groupName,
        AuthDto auth
    ) {
        login(auth);
        Group group = findGroup(groupName);
        checkAuthUserIsMember(auth, group);
        group.setEncryptedPasswords(newEncryptedPassword);
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
        return CurrentUserDto.builder()
            .username(user.getUsername())
            .publicKey(user.getPublicKey())
            .encryptedPrivateKey(user.getEncryptedPrivateKey())
            .inbox(user.getInbox())
            .build();
    }

    public UserDto getUser(String username, AuthDto auth) {
        login(auth);
        User user = findUser(username);
        return toDto(user);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
            .username(user.getUsername())
            .publicKey(user.getPublicKey())
            .build();
    }

    public List<UserDto> getGroupMembers(String groupName, AuthDto auth) {
        login(auth);
        Group group = findGroup(groupName);
        return group.getMembers()
            .stream()
            .map(this::toDto)
            .toList();
    }
}
