package com.chalkim.orinote.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chalkim.orinote.dao.NoteDao;
import com.chalkim.orinote.dto.NoteCreateDto;
import com.chalkim.orinote.dto.NoteUpdateDto;
import com.chalkim.orinote.exception.NoteNotFoundException;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.service.NoteService;

@Service
public class NoteServiceImpl implements NoteService {
    private final NoteDao noteDao;

    public NoteServiceImpl(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    @Override
    @Transactional
    public Note saveNote(NoteCreateDto dto) {
        return noteDao.createNote(dto);
    }

    @Override
    public Note getNoteById(Long id) {
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

    @Override
    public List<Note> getNotesBetween(Instant from, Instant to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be before 'to'");
        }
        return noteDao.getNotesCreatedBetween(from, to);
    }

    @Override
    @Transactional
    public void patchNote(Long id, NoteUpdateDto dto) {
        boolean exists = noteDao.existsById(id);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }

        noteDao.updateNote(id, dto);
    }

    @Override
    @Transactional
    public void softDeleteNote(Long id) {
        int rows = noteDao.softDeleteNote(id);
        if (rows == 0) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
    }
}
