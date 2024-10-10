package ru.ntrubkin.untrusted.warden;

public class Client {
    private String username;
    private String password;

    public void createUser(String username, String password) {
        System.out.println("hw");
        System.out.println(username);
        System.out.println(password);
    }

    public void login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void createGroup(String name) {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void addUserToGroup(String username, String groupName) {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void removeUserToGroup(String username, String groupName) {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void removePasswordFromGroup(String username, String passwordName) {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void addPasswordToGroup(String username, String passwordName, String password) {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }
}
