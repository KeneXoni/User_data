package com.university.usermanager.repository;

import com.university.usermanager.config.DatabaseConfig;
import com.university.usermanager.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    private Connection getConn() {
        return DatabaseConfig.getConnection();
    }

    public void save(User user) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження користувача: " + e.getMessage(), e);
        }
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT id, name, email, password FROM users WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку користувача: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        String sql = "SELECT id, name, email, password FROM users";
        List<User> users = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка отримання користувачів: " + e.getMessage(), e);
        }
        return users;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення користувача: " + e.getMessage(), e);
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM users WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку за email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password")
        );
    }
}
