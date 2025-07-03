package com.chalkim.orinote.dao;

import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chalkim.orinote.model.Summary;

@Repository
public class SummaryDao {
    private final JdbcTemplate jdbc;

    public SummaryDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Summary createSummary(String title, String content, Instant startAt, Instant endAt) {
        try {
            String sql = "INSERT INTO summaries (title, content, is_deleted, start_at, end_at) VALUES (?, ?, false, ?, ?) RETURNING *";
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Summary.class), title, content, startAt, endAt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create summary", e);
        }
    }

    public Summary getSummaryById(Long id) {
        try {
            String sql = "SELECT * FROM summaries WHERE id = ? AND is_deleted = false";
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Summary.class), id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find summary by id", e);
        }
    }

    public List<Summary> getSummaryCreatedBetween(Instant from, Instant to) {
        try {
            String sql = "SELECT * FROM summaries WHERE created_at BETWEEN ? AND ? AND is_deleted = false ORDER BY created_at DESC";
            return jdbc.query(sql, new BeanPropertyRowMapper<>(Summary.class), from, to);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find summaries by range", e);
        }
    }

    public List<Summary> getAllSummaries() {
        try {
            String sql = "SELECT * FROM summaries WHERE is_deleted = false ORDER BY created_at DESC";
            return jdbc.query(sql, new BeanPropertyRowMapper<>(Summary.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find all summaries", e);
        }
    }

    public void updateSummary(Long id, String title, String content) {
        try {
            String sql = "UPDATE summaries SET title = ?, content = ?, updated_at = NOW() WHERE id = ? AND is_deleted = false";
            jdbc.update(sql, title, content, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update summary", e);
        }
    }

    public void softDeleteSummary(Long id) {
        try {
            String sql = "UPDATE summaries SET is_deleted = true, updated_at = NOW() WHERE id = ?";
            jdbc.update(sql, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete summary", e);
        }
    }
}
