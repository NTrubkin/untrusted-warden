package ru.ntrubkin.untrusted.warden;

import java.io.Console;

public class Main {

    private Console console;
    private Client client;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        client = new Client();
        console = System.console();
        menu(client);
    }

    private void menu(Client client) {
        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.println("1. Создать юзера");
            System.out.println("2. Показать список юзеров");
            System.out.println("3. Залогиниться");
            System.out.println("4. Создать группу");
            System.out.println("5. Добавить юзера в группу");
            System.out.println("6. Удалить юзера из группы");
            System.out.println("7. Показать юзеров группы");
            System.out.println("8. Добавить пароль в группу");
            System.out.println("9. Удалить пароль из группы");
            System.out.println("10. Удалить пароли группы");
            String command = console.readLine("Введи номер команды: ");
            switch (command) {
                case "1" -> createUser();
                case "2" -> showUsers();
                case "3" -> login();
                case "4" -> createGroup();
                case "5" -> addUserToGroup();
                case "6" -> removeUserFromGroup();
                case "7" -> showGroupUsers();
                case "8" -> addPasswordToGroup();
                case "9" -> removePasswordFromGroup();
                case "10" -> showGroupPasswords();
            }
        }
    }

    private void showGroupPasswords() {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    private void showGroupUsers() {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    private void removePasswordFromGroup() {
        String username = console.readLine("Введи юзернейм: ");
        String passwordName = console.readLine("Введи название пароля: ");
        client.removePasswordFromGroup(username, passwordName);
    }

    private void addPasswordToGroup() {
        String username = console.readLine("Введи юзернейм: ");
        String passwordName = console.readLine("Введи название пароля: ");
        String password = console.readLine("Введи пароль: ");
        client.addPasswordToGroup(username, passwordName, password);
    }

    private void removeUserFromGroup() {
        String username = console.readLine("Введи юзернейм: ");
        String groupName = console.readLine("Введи название группы: ");
        client.removeUserToGroup(username, groupName);
    }

    private void addUserToGroup() {
        String username = console.readLine("Введи юзернейм: ");
        String groupName = console.readLine("Введи название группы: ");
        client.addUserToGroup(username, groupName);
    }

    private void showUsers() {
        // todo: implement this
        throw new UnsupportedOperationException("not implemented yet");
    }

    private void createGroup() {
        String name = console.readLine("Введи название группы: ");
        client.createGroup(name);
    }

    private void createUser() {
        String username = console.readLine("Введи юзернейм: ");
        char[] password = console.readPassword("Введи пароль: ");
        client.createUser(username, String.valueOf(password));
    }

    private void login() {
        String username = console.readLine("Введи юзернейм: ");
        char[] password = console.readPassword("Введи пароль: ");
        client.login(username, String.valueOf(password));
    }


}