package ru.ntrubkin.untrusted.warden;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ntrubkin.untrusted.warden.component.AsymmetricEncryptor;
import ru.ntrubkin.untrusted.warden.component.ServicePasswordHasher;
import ru.ntrubkin.untrusted.warden.component.SymmetricEncryptor;
import ru.ntrubkin.untrusted.warden.dto.GroupDto;
import ru.ntrubkin.untrusted.warden.dto.UserDto;

import java.util.Scanner;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class Main {

    private Client client;
    private Scanner scanner;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        ObjectMapper objectMapper = new ObjectMapper()
            .disable(FAIL_ON_UNKNOWN_PROPERTIES);
        client = new Client(
            new Server(),
            objectMapper,
            new ServicePasswordHasher(),
            new SymmetricEncryptor(),
            new AsymmetricEncryptor()
        );
        scanner = new Scanner(System.in);
        menu(client);
    }

    private void menu(Client client) {
        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.println("1. Создать юзера");
            System.out.println("2. Показать список юзеров");
            System.out.println("3. Залогиниться");
            System.out.println("4. Создать группу");
            System.out.println("5. Показать мои группы");
            System.out.println("6. Добавить юзера в группу");
            System.out.println("7. Удалить юзера из группы");
            System.out.println("8. Показать юзеров группы");
            System.out.println("9. Добавить пароль в группу");
            System.out.println("10. Удалить пароль из группы");
            System.out.println("11. Показать пароли группы");
            String command = readLine("Введи номер команды: ");
            tryRun(() -> {
                switch (command) {
                    case "1" -> createUser();
                    case "2" -> showUsers();
                    case "3" -> login();
                    case "4" -> createGroup();
                    case "5" -> showMyGroups();
                    case "6" -> addUserToGroup();
                    case "7" -> removeUserFromGroup();
                    case "8" -> showGroupUsers();
                    case "9" -> addPasswordToGroup();
                    case "10" -> removePasswordFromGroup();
                    case "11" -> showGroupPasswords();
                }
            });
        }
    }

    private String readLine(String label) {
        System.out.print(label);
        return scanner.nextLine();
    }

    private void tryRun(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            System.out.println("Произошла ошибка " + e.getClass().getSimpleName() + ": " + e.getMessage());
            pause();
            System.out.println();
        }
    }

    private void pause() {
        System.out.print("Для продолжения введи enter...");
        scanner.nextLine();
    }

    // 1
    private void createUser() {
        String username = readLine("Введи юзернейм: ");
        String password = readLine("Введи пароль: ");
        client.createUser(username, password);
        System.out.println();
    }

    // 2
    private void showUsers() {
        System.out.println("Юзеры: ");
        client.getUsers()
            .stream()
            .map(UserDto::username)
            .map(username -> "- " + username)
            .forEach(System.out::println);
        pause();
        System.out.println();
    }

    // 3
    private void login() {
        String username = readLine("Введи юзернейм: ");
        String password = readLine("Введи пароль: ");
        client.login(username, password);
        System.out.println();
    }

    // 4
    private void createGroup() {
        String name = readLine("Введи название группы: ");
        client.createGroup(name);
        System.out.println();
    }

    // 5
    private void showMyGroups() {
        System.out.println("Мои группы: ");
        client.getMyGroups()
            .stream()
            .map(GroupDto::name)
            .map(groupName -> "- " + groupName)
            .forEach(System.out::println);
        pause();
        System.out.println();
    }

    //6
    private void addUserToGroup() {
        String username = readLine("Введи юзернейм: ");
        String groupName = readLine("Введи название группы: ");
        client.addUserToGroup(username, groupName);
        System.out.println();
    }

    // 7
    private void removeUserFromGroup() {
        String username = readLine("Введи юзернейм: ");
        String groupName = readLine("Введи название группы: ");
        client.removeUserFromGroup(username, groupName);
        System.out.println();
    }

    // 8
    private void showGroupUsers() {
        String groupName = readLine("Введи название группы: ");
        System.out.println("Юзеры группы " + groupName + ": ");
        client.getGroup(groupName)
            .members()
            .stream()
            .map(username -> "- " + username)
            .forEach(System.out::println);
        pause();
        System.out.println();
    }

    // 9
    private void addPasswordToGroup() {
        String passwordName = readLine("Введи название пароля: ");
        String password = readLine("Введи пароль: ");
        String groupName = readLine("Введи название группы: ");
        client.addPasswordToGroup(passwordName, password, groupName);
        System.out.println();
    }

    // 10
    private void removePasswordFromGroup() {
        String passwordName = readLine("Введи название пароля: ");
        String groupName = readLine("Введи название группы: ");
        client.removePasswordFromGroup(passwordName, groupName);
        System.out.println();
    }

    // 11
    private void showGroupPasswords() {
        String groupName = readLine("Введи название группы: ");
        System.out.println("Пароли группы " + groupName + ": ");
        client.getGroupPasswords(groupName)
            .entrySet()
            .stream()
            .map(password -> "- " + password.getKey() + ": " + password.getValue())
            .forEach(System.out::println);
        pause();
        System.out.println();
    }
}
