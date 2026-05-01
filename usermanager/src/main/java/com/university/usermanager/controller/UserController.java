package com.university.usermanager.controller;

import com.university.usermanager.entity.User;
import com.university.usermanager.exception.UserNotFoundException;
import com.university.usermanager.exception.ValidationException;
import com.university.usermanager.service.UserService;

import java.util.List;
import java.util.Scanner;

public class UserController {

    private final UserService userService;
    private final Scanner scanner;

    public UserController() {
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Підключення до бази даних...");
        // Ініціалізуємо з'єднання одразу при запуску
        try {
            userService.getAllUsers();
            System.out.println("З'єднання встановлено успішно!\n");
        } catch (Exception e) {
            System.out.println("Помилка підключення до БД: " + e.getMessage());
            return;
        }

        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> addUser();
                case "2" -> findUserById();
                case "3" -> showAllUsers();
                case "4" -> deleteUser();
                case "0" -> {
                    System.out.println("До побачення!");
                    return;
                }
                default -> System.out.println("Невідома команда. Оберіть пункт меню від 0 до 4.\n");
            }
        }
    }

    private void printMenu() {
        System.out.println("--- Головне меню ---");
        System.out.println("1. Додати користувача");
        System.out.println("2. Знайти користувача за ID");
        System.out.println("3. Показати всіх користувачів");
        System.out.println("4. Видалити користувача");
        System.out.println("0. Вихід");
        System.out.print("Оберіть дію: ");
    }

    private void addUser() {
        System.out.println("\n--- Додати користувача ---");
        System.out.print("Ім'я: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();

        try {
            User created = userService.createUser(name, email, password);
            System.out.println("Користувача успішно створено: " + created + "\n");
        } catch (ValidationException e) {
            System.out.println("Помилка валідації: " + e.getMessage() + "\n");
        }
    }

    private void findUserById() {
        System.out.println("\n--- Пошук за ID ---");
        System.out.print("Введіть ID: ");
        String input = scanner.nextLine().trim();

        try {
            Long id = Long.parseLong(input);
            User user = userService.getUserById(id);
            System.out.println("Знайдено: " + user + "\n");
        } catch (NumberFormatException e) {
            System.out.println("Помилка: ID повинен бути числом\n");
        } catch (UserNotFoundException e) {
            System.out.println("Користувача не знайдено: " + e.getMessage() + "\n");
        }
    }

    private void showAllUsers() {
        System.out.println("\n--- Всі користувачі ---");
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Список користувачів порожній\n");
        } else {
            users.forEach(System.out::println);
            System.out.println("Всього: " + users.size() + "\n");
        }
    }

    private void deleteUser() {
        System.out.println("\n--- Видалити користувача ---");
        System.out.print("Введіть ID: ");
        String input = scanner.nextLine().trim();

        try {
            Long id = Long.parseLong(input);
            userService.deleteUser(id);
            System.out.println("Користувача з ID=" + id + " успішно видалено\n");
        } catch (NumberFormatException e) {
            System.out.println("Помилка: ID повинен бути числом\n");
        } catch (UserNotFoundException e) {
            System.out.println("Користувача не знайдено: " + e.getMessage() + "\n");
        }
    }
}
