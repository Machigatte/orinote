package com.chalkim.orinote.service.impl;

import com.chalkim.orinote.dto.SearchNoteDto;
import java.time.Instant;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.chalkim.orinote.dao.NoteDao;
import com.chalkim.orinote.dto.NoteDto;
import com.chalkim.orinote.exception.NoteNotFoundException;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.service.NoteService;

@Validated
@Service
public class NoteServiceImpl implements NoteService {
    private final NoteDao noteDao;

    public NoteServiceImpl(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    @Override
    @Transactional
    public Note saveNote(@Valid @NotNull NoteDto dto) {
        return noteDao.createNote(dto);
    }

    @Override
    public Note getNoteById(@NotNull Long id) {
        try {
            return noteDao.getNoteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
    }

    @Override
    public List<Note> getAllNotes() {
        return noteDao.getAllNotes();
    }

    // @Override
    // public List<Note> getNotesBetween(@NotNull Instant from, @NotNull Instant to) {
    //     if (from.isAfter(to)) {
    //         throw new IllegalArgumentException("'from' must be before 'to'");
    //     }
    //     return noteDao.getNotesCreatedBetween(from, to);
    // }
    @Override
    public List<Note> searchNotes(SearchNoteDto searchDto) {
        return noteDao.searchNotes(searchDto);
    }


    @Override
    @Transactional
    public void updateNote(@NotNull Long id, @Valid @NotNull NoteDto dto) {
        boolean exists = noteDao.existsById(id);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }

        noteDao.updateNote(id, dto);
    }

    @Override
    @Transactional
    public void softDeleteNote(@NotNull Long id) {
        int rows = noteDao.softDeleteNote(id);
        if (rows == 0) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
    }
}
