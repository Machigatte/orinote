package com.chalkim.orinote.dao;

import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chalkim.orinote.model.Note;

@Repository
public class NoteDao {
    private final JdbcTemplate jdbc;

    public NoteDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Note createNote(String title, String content) {
        try {
            String sql = "INSERT INTO notes (title, content, is_deleted, created_at, updated_at) VALUES (?, ?, false, NOW(), NOW()) RETURNING *";
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Note.class), title, content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create note", e);
        }
    }

    public Note getNoteById(Long id) {
        try {
            String sql = "SELECT * FROM notes WHERE id = ? AND is_deleted = false";
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Note.class), id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find note by id", e);
        }
    }

    public List<Note> getNotesCreatedBetween(Instant from, Instant to) {
        try {
            String sql = "SELECT * FROM notes WHERE created_at BETWEEN ? AND ? AND is_deleted = false ORDER BY created_at DESC";
            return jdbc.query(sql, new BeanPropertyRowMapper<>(Note.class), from, to);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find notes by range", e);
        }
    }

    public List<Note> getAllNotes() {
        try {
            String sql = "SELECT * FROM notes WHERE is_deleted = false ORDER BY created_at DESC";
            return jdbc.query(sql, new BeanPropertyRowMapper<>(Note.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find all notes", e);
        }
    }

    public void updateNote(Long id, String title, String content) {
        try {
            String sql = "UPDATE notes SET title = ?, content = ?, updated_at = NOW() WHERE id = ? AND is_deleted = false";
            jdbc.update(sql, title, content, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update note", e);
        }
    }

    public void softDeleteNote(Long id) {
        try {
            String sql = "UPDATE notes SET is_deleted = true, updated_at = NOW() WHERE id = ?";
            jdbc.update(sql, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete note", e);
        }
    }
}
