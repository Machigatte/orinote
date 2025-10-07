package com.chalkim.orinote.dao.impl;

import com.chalkim.orinote.dao.UserDao;
import com.chalkim.orinote.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswd(rs.getString("passwd"));
        user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
        return user;
    };

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, passwd, created_at, updated_at FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username, passwd, created_at, updated_at FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    private User insert(User user) {
        String sql = "INSERT INTO users (username, passwd, created_at, updated_at) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswd());
            ps.setTimestamp(3, Timestamp.from(user.getCreatedAt()));
            ps.setTimestamp(4, Timestamp.from(user.getUpdatedAt()));
            return ps;
        }, keyHolder);

        if (!keyHolder.getKeyList().isEmpty()) {
            Object idObj = keyHolder.getKeyList().get(0).get("id");
            if (idObj != null) {
                user.setId(Long.parseLong(idObj.toString()));
            }
        }
        return user;
    }

    private User update(User user) {
        String sql = "UPDATE users SET username = ?, passwd = ?, updated_at = ? WHERE id = ?";
        
        user.setUpdatedAt(Instant.now());
        
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPasswd(),
                Timestamp.from(user.getUpdatedAt()),
                user.getId());
        
        return user;
    }
}