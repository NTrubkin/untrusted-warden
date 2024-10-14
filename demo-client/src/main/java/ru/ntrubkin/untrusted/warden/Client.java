package ru.ntrubkin.untrusted.warden;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.ntrubkin.untrusted.warden.component.AsymmetricEncryptor;
import ru.ntrubkin.untrusted.warden.component.ServicePasswordHasher;
import ru.ntrubkin.untrusted.warden.component.SymmetricEncryptor;
import ru.ntrubkin.untrusted.warden.dto.AuthDto;
import ru.ntrubkin.untrusted.warden.dto.CurrentUserDto;
import ru.ntrubkin.untrusted.warden.dto.GroupDto;
import ru.ntrubkin.untrusted.warden.dto.UserDto;

import java.security.KeyPair;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class Client {

    private static final TypeReference<Map<String, String>> MAP_STRING_STRING_TYPE_REF = new TypeReference<>() {
    };
    private final Server server;
    private final ObjectMapper objectMapper;
    private final ServicePasswordHasher servicePasswordHasher;
    private final SymmetricEncryptor symmetricEncryptor;
    private final AsymmetricEncryptor asymmetricEncryptor;
    private AuthDto auth;
    private String rawPassword;

    public void createUser(String username, String password) {
        String servicePassword = servicePasswordHasher.hash(password);
        KeyPair keyPair = asymmetricEncryptor.generateKeyPair();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] encryptedPrivateKey = symmetricEncryptor.encrypt(privateKey, password);
        server.registerUser(username, servicePassword, publicKey, encryptedPrivateKey);
    }

    public void login(String username, String password) {
        String servicePassword = servicePasswordHasher.hash(password);
        AuthDto newAuth = new AuthDto(username, servicePassword);
        server.login(newAuth);
        auth = newAuth;
        rawPassword = password;
    }

    public List<UserDto> getUsers() {
        return server.getUsers(auth);
    }

    @SneakyThrows
    public void createGroup(String name) {
        Map<String, String> passwords = Map.of();
        byte[] serializedPasswords = objectMapper.writeValueAsBytes(passwords);

        var groupKey = randomUUID().toString();
        byte[] encryptedPasswords = symmetricEncryptor.encrypt(serializedPasswords, groupKey);

        CurrentUserDto currentUser = server.getCurrentUser(auth);
        byte[] encryptedGroupKey = asymmetricEncryptor.encrypt(groupKey.getBytes(), currentUser.publicKey());
        server.createGroup(name, encryptedPasswords, encryptedGroupKey, auth);
    }

    public List<GroupDto> getMyGroups() {
        return server.getMyGroups(auth);
    }

    public void addUserToGroup(String username, String groupName) {
        byte[] groupKey = getGroupKey(groupName);

        UserDto user = server.getUser(username, auth);
        byte[] newEncryptedGroupKey = asymmetricEncryptor.encrypt(groupKey, user.publicKey());
        server.addUserToGroup(username, groupName, newEncryptedGroupKey, auth);
    }

    private byte[] getGroupKey(String groupName) {
        CurrentUserDto currentUser = server.getCurrentUser(auth);
        byte[] encryptedGroupKey = currentUser.inbox().get(groupName);
        byte[] privateKey = symmetricEncryptor.decrypt(currentUser.encryptedPrivateKey(), rawPassword);
        return asymmetricEncryptor.decrypt(encryptedGroupKey, privateKey);
    }

    @SneakyThrows
    public void removeUserFromGroup(String username, String groupName) {
        GroupDto group = server.getGroup(groupName, auth);
        byte[] encryptedPasswords = group.encryptedPasswords();
        byte[] groupKey = getGroupKey(groupName);

        byte[] serializedPasswords = symmetricEncryptor.decrypt(encryptedPasswords, new String(groupKey));
        var newGroupKey = randomUUID().toString();
        byte[] newEncryptedPasswords = symmetricEncryptor.encrypt(serializedPasswords, newGroupKey);

        List<UserDto> groupMembers = server.getGroupMembers(groupName, auth);
        Map<String, byte[]> inboxUpdates = groupMembers.stream()
            .collect(toMap(
                UserDto::username,
                member -> asymmetricEncryptor.encrypt(newGroupKey.getBytes(), member.publicKey())
            ));

        server.removeUserFromGroup(username, groupName, newEncryptedPasswords, inboxUpdates, auth);
    }

    public void addPasswordToGroup(String passwordName, String password, String groupName) {
        openGroupPasswords(passwords -> passwords.put(passwordName, password), groupName);
    }

    @SneakyThrows
    private void openGroupPasswords(Consumer<Map<String, String>> passwordsHandler, String groupName) {
        GroupDto group = server.getGroup(groupName, auth);
        byte[] encryptedPasswords = group.encryptedPasswords();
        byte[] groupKey = getGroupKey(groupName);

        byte[] serializedPasswords = symmetricEncryptor.decrypt(encryptedPasswords, new String(groupKey));
        Map<String, String> passwords = objectMapper.readValue(serializedPasswords, MAP_STRING_STRING_TYPE_REF);
        passwordsHandler.accept(passwords);
        byte[] newSerializedPasswords = objectMapper.writeValueAsBytes(passwords);
        byte[] newEncryptedPasswords = symmetricEncryptor.encrypt(newSerializedPasswords, new String(groupKey));
        server.updateGroupPasswords(newEncryptedPasswords, groupName, auth);
    }

    public void removePasswordFromGroup(String passwordName, String groupName) {
        openGroupPasswords(passwords -> passwords.remove(passwordName), groupName);
    }

    public GroupDto getGroup(String groupName) {
        return server.getGroup(groupName, auth);
    }

    @SneakyThrows
    public Map<String, String> getGroupPasswords(String groupName) {
        GroupDto group = server.getGroup(groupName, auth);
        byte[] encryptedPasswords = group.encryptedPasswords();
        byte[] groupKey = getGroupKey(groupName);

        byte[] serializedPasswords = symmetricEncryptor.decrypt(encryptedPasswords, new String(groupKey));
        return objectMapper.readValue(serializedPasswords, MAP_STRING_STRING_TYPE_REF);
    }
}
