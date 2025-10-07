package com.chalkim.orinote.dao;

import com.chalkim.orinote.dto.SearchNoteDto;
import java.sql.Timestamp;
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

    public boolean existsById(Long id, Long userId) {
        String sql = "SELECT COUNT(*) FROM notes WHERE id = ? AND user_id = ? AND is_deleted = false";
        Integer count = jdbc.queryForObject(sql, Integer.class, id, userId);
        return count != null && count > 0;
    }

    public boolean isArchived(Long id, Long userId) {
        String sql = "SELECT COUNT(*) FROM notes WHERE id = ? AND user_id = ? AND archived_at IS NOT NULL";
        Integer count = jdbc.queryForObject(sql, Integer.class, id, userId);
        return count != null && count > 0;
    }

        public Note createNote(NoteDto dto, Long userId) {
                String sql = """
                                INSERT INTO notes (title, note_type, head, body, tail, summary, user_id,
                                    is_deleted, archived_at, created_at, updated_at)
                                VALUES (?, ?, ?, ?, ?, ?, ?,
                                    false, null, NOW(), NOW()) RETURNING *
                                """;
                return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Note.class),
                                dto.getTitle(), dto.getNoteType(), dto.getHead(), dto.getBody(), dto.getTail(), dto.getSummary(), userId);
        }

    public Note getNoteById(Long id, Long userId) {
        String sql = "SELECT * FROM notes WHERE id = ? AND user_id = ? AND is_deleted = false";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Note.class), id, userId);
    }

        public List<Note> getNotesCreatedBetween(Long userId, Instant from, Instant to) {
                String sql = """
                                SELECT * FROM notes
                                WHERE user_id = ? AND created_at BETWEEN ? AND ? AND
                                    is_deleted = false ORDER BY created_at DESC
                                """;
                Timestamp sqlFrom = Timestamp.from(from);
                Timestamp sqlTo = Timestamp.from(to);
                return jdbc.query(sql, new BeanPropertyRowMapper<>(Note.class), userId, sqlFrom, sqlTo);
        }

    public List<Note> getAllNotes(Long userId) {
        String sql = "SELECT * FROM notes WHERE user_id = ? AND is_deleted = false ORDER BY created_at DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Note.class), userId);
    }

    public int updateNote(Long id, Long userId, NoteDto dto) {
    String sql = """
        UPDATE notes SET
          title = COALESCE(?, title),
          note_type = COALESCE(?, note_type),
          head = COALESCE(?, head),
          body = COALESCE(?, body),
          tail = COALESCE(?, tail),
          summary = COALESCE(?, summary),
          updated_at = NOW() WHERE id = ? AND user_id = ? AND is_deleted = false
        """;
    return jdbc.update(sql,
        dto.getTitle(),
        dto.getNoteType(),
        dto.getHead(),
        dto.getBody(),
        dto.getTail(),
        dto.getSummary(),
        id,
        userId);
    }

    public Note archiveNote(Long id, Long userId) {
        String sql = "UPDATE notes SET archived_at = NOW() WHERE id = ? AND user_id = ? RETURNING *";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Note.class), id, userId);
    }

    public int updateArchivedAt(Long id, Long userId, NoteDto dto) {
    String sql = """
        UPDATE notes SET
          title = COALESCE(?, title),
          note_type = COALESCE(?, note_type),
          head = COALESCE(?, head),
          body = COALESCE(?, body),
          tail = COALESCE(?, tail),
          summary = COALESCE(?, summary),
          archived_at = NOW(),
          updated_at = NOW()
        WHERE id = ? AND user_id = ? AND is_deleted = false
        """;
    return jdbc.update(sql,
        dto.getTitle(),
        dto.getNoteType(),
        dto.getHead(),
        dto.getBody(),
        dto.getTail(),
        dto.getSummary(),
        id,
        userId);
    }

    public int softDeleteNote(Long id, Long userId) {
        String sql = "UPDATE notes SET is_deleted = true, updated_at = NOW() WHERE id = ? AND user_id = ?";
        return jdbc.update(sql, id, userId);
    }

    public int analyseNote(Long id, Long userId, NoteDto dto) {
    String sql = """
        UPDATE notes SET
          title = COALESCE(?, title),
          note_type = COALESCE(?, note_type),
          head = COALESCE(?, head),
          body = COALESCE(?, body),
          tail = COALESCE(?, tail),
          summary = COALESCE(?, summary),
          archived_at = NOW(),
          updated_at = NOW()
        WHERE id = ? AND user_id = ? AND is_deleted = false
        """;
    return jdbc.update(sql,
        dto.getTitle(),
        dto.getNoteType(),
        dto.getHead(),
        dto.getBody(),
        dto.getTail(),
        dto.getSummary(),
        id,
        userId);
    }


    public List<Note> searchNotes(Long userId, SearchNoteDto searchDto) {
        StringBuilder sql = new StringBuilder("SELECT * FROM notes WHERE user_id = ? AND is_deleted = false");
        List<Object> params = new ArrayList<>();
        params.add(userId);

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
