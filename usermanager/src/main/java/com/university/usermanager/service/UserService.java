package com.university.usermanager.service;

import com.university.usermanager.entity.User;
import com.university.usermanager.exception.UserNotFoundException;
import com.university.usermanager.exception.ValidationException;
import com.university.usermanager.repository.UserRepository;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User createUser(String name, String email, String password) {
        // Перевірка імені
        if (name == null || name.isBlank()) {
            throw new ValidationException("Ім'я не може бути порожнім");
        }

        // Перевірка email
        if (email == null || !email.contains("@")) {
            throw new ValidationException("Некоректний email. Email повинен містити символ '@'");
        }

        // Перевірка пароля
        if (password == null || password.length() < 6) {
            throw new ValidationException("Пароль занадто короткий. Мінімум 6 символів");
        }

        // Перевірка на дублікат email
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("Користувач з email '" + email + "' вже існує");
        }

        User user = new User(name, email, password);
        userRepository.save(user);
        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Користувача з ID=" + id + " не знайдено"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        // Спочатку перевіряємо що користувач існує
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Користувача з ID=" + id + " не знайдено"));
        userRepository.deleteById(id);
    }
}
