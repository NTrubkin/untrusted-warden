package ru.ntrubkin.untrusted.warden;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.ntrubkin.untrusted.warden.component.AsymmetricEncryptor;
import ru.ntrubkin.untrusted.warden.component.ServicePasswordHasher;
import ru.ntrubkin.untrusted.warden.component.SymmetricEncryptor;
import ru.ntrubkin.untrusted.warden.dto.AuthDto;
import ru.ntrubkin.untrusted.warden.dto.GroupDto;
import ru.ntrubkin.untrusted.warden.dto.UserDto;

import java.security.KeyPair;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
public class Client {

    private final Server server;
    private final ObjectMapper objectMapper;
    private final ServicePasswordHasher servicePasswordHasher;
    private final SymmetricEncryptor symmetricEncryptor;
    private final AsymmetricEncryptor asymmetricEncryptor;
    private AuthDto auth;

    public void createUser(String username, String password) {
        String servicePassword = servicePasswordHasher.hash(password);
        KeyPair keyPair = asymmetricEncryptor.generateKeyPair();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String encryptedPrivateKey = symmetricEncryptor.encrypt(privateKey, password);
        server.registerUser(username, servicePassword, publicKey, encryptedPrivateKey);
    }

    public void login(String username, String password) {
        String servicePassword = servicePasswordHasher.hash(password);
        AuthDto newAuth = new AuthDto(username, servicePassword);
        server.login(newAuth);
        auth = newAuth;
    }

    public List<UserDto> getUsers() {
        return server.getUsers(auth);
    }

    public void createGroup(String name) {
        server.createGroup(name, auth);
    }

    public List<GroupDto> getMyGroups() {
        return server.getMyGroups(auth);
    }

    public void addUserToGroup(String username, String groupName) {
        server.addUserToGroup(username, groupName, auth);
    }

    public void removeUserToGroup(String username, String groupName) {
        server.removeUserFromGroup(username, groupName, auth);
    }

    public void addPasswordToGroup(String passwordName, String password, String groupName) {
        server.addPasswordToGroup(passwordName, password, groupName, auth);
    }

    public void removePasswordFromGroup(String passwordName, String groupName) {
        server.removePasswordFromGroup(passwordName, groupName, auth);
    }

    @SneakyThrows
    public GroupDto getGroup(String groupName) {
        String group = server.getGroup(groupName, auth);
        return objectMapper.readValue(group, GroupDto.class);
    }
}
