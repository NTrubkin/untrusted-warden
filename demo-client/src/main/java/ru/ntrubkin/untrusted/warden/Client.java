package ru.ntrubkin.untrusted.warden;

import ru.ntrubkin.untrusted.warden.dto.AuthDto;
import ru.ntrubkin.untrusted.warden.dto.GroupDto;
import ru.ntrubkin.untrusted.warden.dto.UserDto;

import java.util.List;

public class Client {

    private final Server server;
    private AuthDto auth;

    public Client(Server server) {
        this.server = server;
    }

    public void createUser(String username, String password) {
        server.registerUser(username, password);
    }

    public void login(String username, String password) {
        AuthDto newAuth = new AuthDto(username, password);
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

    public GroupDto getGroup(String groupName) {
        return server.getGroup(groupName, auth);
    }
}
