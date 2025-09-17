package com.chalkim.orinote.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chalkim.orinote.model.UserOAuthAccount;

@Repository
public class UserOAuthAccountDao {
    private final JdbcTemplate jdbc;

    public UserOAuthAccountDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<UserOAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId) {
        String sql = "SELECT * FROM user_oauth_accounts WHERE provider = ? AND provider_user_id = ?";
        try {
            UserOAuthAccount account = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(UserOAuthAccount.class),
                    provider, providerUserId);
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<UserOAuthAccount> findByUserId(Long userId) {
        String sql = "SELECT * FROM user_oauth_accounts WHERE user_id = ?";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(UserOAuthAccount.class), userId);
    }

    public UserOAuthAccount save(UserOAuthAccount account) {
        if (account.getId() == null) {
            return create(account);
        } else {
            return update(account);
        }
    }

    private UserOAuthAccount create(UserOAuthAccount account) {
        String sql = """
                INSERT INTO user_oauth_accounts (user_id, provider, provider_user_id, created_at, updated_at)
                VALUES (?, ?, ?, NOW(), NOW()) RETURNING *
                """;
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(UserOAuthAccount.class),
                account.getUserId(), account.getProvider(), account.getProviderUserId());
    }

    private UserOAuthAccount update(UserOAuthAccount account) {
        String sql = """
                UPDATE user_oauth_accounts SET
                    user_id = ?,
                    provider = ?,
                    provider_user_id = ?,
                    updated_at = NOW()
                WHERE id = ? RETURNING *
                """;
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(UserOAuthAccount.class),
                account.getUserId(), account.getProvider(), account.getProviderUserId(), account.getId());
    }

    public boolean existsByProviderAndProviderUserId(String provider, String providerUserId) {
        String sql = "SELECT COUNT(*) FROM user_oauth_accounts WHERE provider = ? AND provider_user_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, provider, providerUserId);
        return count != null && count > 0;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM user_oauth_accounts WHERE id = ?";
        jdbc.update(sql, id);
    }

    public void deleteByProviderAndProviderUserId(String provider, String providerUserId) {
        String sql = "DELETE FROM user_oauth_accounts WHERE provider = ? AND provider_user_id = ?";
        jdbc.update(sql, provider, providerUserId);
    }

    public List<UserOAuthAccount> findAll() {
        String sql = "SELECT * FROM user_oauth_accounts ORDER BY created_at DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(UserOAuthAccount.class));
    }
}