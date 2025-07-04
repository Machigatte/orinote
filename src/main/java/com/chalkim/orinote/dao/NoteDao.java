package com.chalkim.orinote.dao;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chalkim.orinote.dto.NoteDto;
import com.chalkim.orinote.model.Note;

@Repository
public class NoteDao {
    private final JdbcTemplate jdbc;

    public NoteDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM notes WHERE id = ? AND is_deleted = false";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public Note createNote(NoteDto dto) {
        String sql = "INSERT INTO notes (title, content, is_deleted, created_at, updated_at) VALUES (?, ?, false, NOW(), NOW()) RETURNING *";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Note.class), dto.getTitle(), dto.getContent());
    }

    public Note getNoteById(Long id) {
        String sql = "SELECT * FROM notes WHERE id = ? AND is_deleted = false";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Note.class), id);
    }

    public List<Note> getNotesCreatedBetween(Instant from, Instant to) {
        String sql = "SELECT * FROM notes WHERE created_at BETWEEN ? AND ? AND is_deleted = false ORDER BY created_at DESC";
        Timestamp sqlFrom = Timestamp.from(from);
        Timestamp sqlTo = Timestamp.from(to);
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Note.class), sqlFrom, sqlTo);
    }

    public List<Note> getAllNotes() {
        String sql = "SELECT * FROM notes WHERE is_deleted = false ORDER BY created_at DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Note.class));
    }

    public int updateNote(Long id, NoteDto dto) {
        String sql = "UPDATE notes SET title = COALESCE(?, title), content = COALESCE(?, content), updated_at = NOW() WHERE id = ? AND is_deleted = false";
        return jdbc.update(sql, dto.getTitle(), dto.getContent(), id);
    }

    public int softDeleteNote(Long id) {
        String sql = "UPDATE notes SET is_deleted = true, updated_at = NOW() WHERE id = ?";
        return jdbc.update(sql, id);
    }
}
