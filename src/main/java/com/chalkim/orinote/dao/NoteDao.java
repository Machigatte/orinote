package com.chalkim.orinote.dao;

import com.chalkim.orinote.dto.SearchNoteDto;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ArrayList;
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
        String sql = """
                INSERT INTO notes (title, note_type, head, body, tail, summary,
                  is_deleted, archived_at, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?,
                  false, null, NOW(), NOW()) RETURNING *
                """;
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Note.class),
                dto.getTitle(), dto.getNoteType(), dto.getHead(), dto.getBody(), dto.getTail(), dto.getSummary());
    }

    public Note getNoteById(Long id) {
        String sql = "SELECT * FROM notes WHERE id = ? AND is_deleted = false";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Note.class), id);
    }

    public List<Note> getNotesCreatedBetween(Instant from, Instant to) {
        String sql = """
                SELECT * FROM notes
                WHERE created_at BETWEEN ? AND ? AND
                  is_deleted = false ORDER BY created_at DESC
                """;
        Timestamp sqlFrom = Timestamp.from(from);
        Timestamp sqlTo = Timestamp.from(to);
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Note.class), sqlFrom, sqlTo);
    }

    public List<Note> getAllNotes() {
        String sql = "SELECT * FROM notes WHERE is_deleted = false ORDER BY created_at DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Note.class));
    }

    public int updateNote(Long id, NoteDto dto) {
        String sql = """
                UPDATE notes SET
                  title = COALESCE(?, title),
                  note_type = COALESCE(?, note_type),
                  head = COALESCE(?, head),
                  body = COALESCE(?, body),
                  tail = COALESCE(?, tail),
                  summary = COALESCE(?, summary),
                  updated_at = NOW() WHERE id = ? AND is_deleted = false
                """;
        return jdbc.update(sql,
                dto.getTitle(),
                dto.getNoteType(),
                dto.getHead(),
                dto.getBody(),
                dto.getTail(),
                dto.getSummary(),
                id);
    }

    public int analyseNote(Long id) {
        String sql = """
                UPDATE notes SET
                  summary = COALESCE(?, summary),
                  updated_at = NOW() WHERE id = ? AND is_deleted = false
                """;

        return jdbc.update(sql,
                "分析中...",
                id);
    }

    public int archiveNote(Long id) {
        String sql = "UPDATE notes SET archived_at = NOW() WHERE id = ?";
        return jdbc.update(sql, id);
    }

    public int softDeleteNote(Long id) {
        String sql = "UPDATE notes SET is_deleted = true, updated_at = NOW() WHERE id = ?";
        return jdbc.update(sql, id);
    }

    public List<Note> searchNotes(SearchNoteDto searchDto) {
        StringBuilder sql = new StringBuilder("SELECT * FROM notes WHERE is_deleted = false");
        List<Object> params = new ArrayList<>();

        if (searchDto.getFrom() != null && searchDto.getTo() != null) {
            sql.append(" AND created_at BETWEEN ? AND ?");
            params.add(Timestamp.from(searchDto.getFrom()));
            params.add(Timestamp.from(searchDto.getTo()));
        } else if (searchDto.getFrom() != null) {
            sql.append(" AND created_at >= ?");
            params.add(Timestamp.from(searchDto.getFrom()));
        } else if (searchDto.getTo() != null) {
            sql.append(" AND created_at <= ?");
            params.add(Timestamp.from(searchDto.getTo()));
        }

        if (searchDto.getNoteType() != null) {
            sql.append(" AND note_type = ?");
            params.add(searchDto.getNoteType());
        }

        if (searchDto.getKeyword() != null && !searchDto.getKeyword().isBlank()) {
            sql.append(" AND (title LIKE ? OR head LIKE ? OR body LIKE ? OR tail LIKE ?)");
            String likeKeyword = "%" + searchDto.getKeyword() + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        sql.append(" ORDER BY created_at DESC");
        return jdbc.query(sql.toString(), new BeanPropertyRowMapper<>(Note.class), params.toArray());
    }
}
