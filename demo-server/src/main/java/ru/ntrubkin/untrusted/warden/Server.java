package ru.ntrubkin.untrusted.warden;

import ru.ntrubkin.untrusted.warden.dto.CreateUserRequest;
import ru.ntrubkin.untrusted.warden.dto.CreateUserResponse;
import ru.ntrubkin.untrusted.warden.model.User;

import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<User> users = new ArrayList<>();

    public CreateUserResponse registerUser(CreateUserRequest request) {
        users.add(new User());
        return new CreateUserResponse();
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
