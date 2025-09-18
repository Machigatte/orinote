package com.chalkim.orinote.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chalkim.orinote.model.User;

@Repository
public class UserDao {
    private final JdbcTemplate jdbc;

    public UserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            User user = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    public User save(User user) {
        if (user.getId() == null) {
            return create(user);
        } else {
            return update(user);
        }
    }

    private User create(User user) {
        String sql = """
                INSERT INTO users (username, email, role, enabled, created_at, updated_at)
                VALUES (?, ?, ?, ?, NOW(), NOW()) RETURNING *
                """;
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),
                user.getUsername(), user.getEmail(), user.getRole(), user.getEnabled());
    }

    private User update(User user) {
        String sql = """
                UPDATE users SET
                    username = ?,
                    email = ?,
                    role = ?,
                    enabled = ?,
                    updated_at = NOW()
                WHERE id = ? RETURNING *
                """;
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),
                user.getUsername(), user.getEmail(), user.getRole(), user.getEnabled(), user.getId());
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbc.update(sql, id);
    }

    public int setEnabled(Long id, boolean enabled) {
        String sql = "UPDATE users SET enabled = ?, updated_at = NOW() WHERE id = ?";
        return jdbc.update(sql, enabled, id);
    }
}