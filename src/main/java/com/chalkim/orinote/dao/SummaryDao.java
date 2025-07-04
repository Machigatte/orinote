package com.chalkim.orinote.dao;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chalkim.orinote.dto.SummaryCreateDto;
import com.chalkim.orinote.dto.SummaryUpdateDto;
import com.chalkim.orinote.model.Summary;

@Repository
public class SummaryDao {
    private final JdbcTemplate jdbc;

    public SummaryDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM summaries WHERE id = ? AND is_deleted = false";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public Summary createSummary(SummaryCreateDto dto) {
        String sql = "INSERT INTO summaries (title, content, is_deleted, start_at, end_at) VALUES (?, ?, false, ?, ?) RETURNING *";
        Timestamp sqlStartAt = Timestamp.from(dto.getStartAt());
        Timestamp sqlEndAt = Timestamp.from(dto.getEndAt());
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Summary.class), dto.getTitle(),
                dto.getContent(), sqlStartAt, sqlEndAt);
    }

    public Summary getSummaryById(Long id) {
        String sql = "SELECT * FROM summaries WHERE id = ? AND is_deleted = false";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Summary.class), id);
    }

    public List<Summary> getSummaryCreatedBetween(Instant from, Instant to) {
        String sql = "SELECT * FROM summaries WHERE created_at BETWEEN ? AND ? AND is_deleted = false ORDER BY created_at DESC";
        Timestamp sqlFrom = Timestamp.from(from);
        Timestamp sqlTo = Timestamp.from(to);
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Summary.class), sqlFrom, sqlTo);
    }

    public List<Summary> getAllSummaries() {
        String sql = "SELECT * FROM summaries WHERE is_deleted = false ORDER BY created_at DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Summary.class));
    }

    public int updateSummary(Long id, SummaryUpdateDto dto) {
        String sql = "UPDATE summaries SET title = COALESCE(?, title), content = COALESCE(?, content), updated_at = NOW() WHERE id = ? AND is_deleted = false";
        return jdbc.update(sql, dto.getTitle(), dto.getContent(), id);
    }

    public int softDeleteSummary(Long id) {
        String sql = "UPDATE summaries SET is_deleted = true, updated_at = NOW() WHERE id = ?";
        return jdbc.update(sql, id);
    }
}
