package ru.ntrubkin.untrusted.warden;

import ru.ntrubkin.untrusted.warden.dto.CreateUserRequest;
import ru.ntrubkin.untrusted.warden.dto.CreateUserResponse;
import ru.ntrubkin.untrusted.warden.model.User;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

public class Server {

    private List<User> users = new ArrayList<>();

    public CreateUserResponse registerUser(CreateUserRequest request) {
        boolean userExists = users.stream()
            .anyMatch(user -> user.username().equals(request.username()));
        if (userExists) {
            throw new RuntimeException("Юзер с таким юзернеймом уже существует");
        }
        User user = User.builder()
            .id(randomUUID())
            .username(request.username())
            .password(request.password())
            .build();
        users.add(user);
        return CreateUserResponse.builder()
            .id(user.id())
            .build();
    }

    public void addGroup() {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void addUserToGroup() {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void removeUserFromGroup() {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void getGroupStorage() {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void popInbox() {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }
}
