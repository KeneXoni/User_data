package com.university.usermanager.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConfig {

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = createConnection();
                initializeDatabase(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка перевірки з'єднання: " + e.getMessage(), e);
        }
        return connection;
    }

    private static Connection createConnection() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл application.properties не знайдено в папці resources/");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Помилка читання application.properties: " + e.getMessage(), e);
        }

        String url      = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        if (url == null || url.isBlank()) {
            throw new RuntimeException("Параметр db.url не задано в application.properties");
        }

        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося підключитися до БД: " + e.getMessage(), e);
        }
    }

    private static void initializeDatabase(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(100) NOT NULL, " +
                     "email VARCHAR(150) NOT NULL UNIQUE, " +
                     "password VARCHAR(255) NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Помилка створення таблиці: " + e.getMessage(), e);
        }
    }
}
