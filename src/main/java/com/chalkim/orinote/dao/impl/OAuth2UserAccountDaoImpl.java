package com.chalkim.orinote.dao.impl;

import com.chalkim.orinote.dao.OAuth2UserAccountDao;
import com.chalkim.orinote.model.OAuth2UserAccount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class OAuth2UserAccountDaoImpl implements OAuth2UserAccountDao {
    private final JdbcTemplate jdbcTemplate;

    public OAuth2UserAccountDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OAuth2UserAccount> rowMapper = new RowMapper<>() {
        @Override
        public OAuth2UserAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new OAuth2UserAccount(
                rs.getString("provider"),
                rs.getString("id_from_provider"),
                rs.getLong("user_id")
            );
        }
    };

    @Override
    public OAuth2UserAccount findByProviderAndId(String provider, String idFromProvider) {
        String sql = "SELECT provider, id_from_provider, user_id FROM oauth2_user WHERE provider = ? AND id_from_provider = ?";
        return jdbcTemplate.query(sql, new Object[]{provider, idFromProvider}, rs -> rs.next() ? rowMapper.mapRow(rs, 0) : null);
    }

    @Override
    public void save(OAuth2UserAccount account) {
        String sql = "INSERT INTO oauth2_user (provider, id_from_provider, user_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, account.getProvider(), account.getIdFromProvider(), account.getUserId());
    }
}
