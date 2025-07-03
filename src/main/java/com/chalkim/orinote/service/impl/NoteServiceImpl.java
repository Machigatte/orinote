package com.chalkim.orinote.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chalkim.orinote.dao.NoteDao;
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
    public Note saveNote(Note note) {
        return noteDao.createNote(note.getTitle(), note.getContent());
    }

    @Override
    public Optional<Note> getNoteById(Long id) {
        Note note = noteDao.getNoteById(id);
        if (note != null) {
            return Optional.of(note);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Note> getAllNotes() {
        return noteDao.getAllNotes();
    }

    @Override
    public List<Note> getNotesBetween(Instant from, Instant to) {
        return noteDao.getNotesCreatedBetween(from, to);
    }

    @Override
    @Transactional
    public void updateNote(Long id, String title, String content) {
        Note existingNote = noteDao.getNoteById(id);
        if (existingNote != null) {
            noteDao.updateNote(id, title, content);
        } else {
            throw new RuntimeException("Note not found");
        }
    }

    @Override
    @Transactional
    public void softDeleteNote(Long id) {
        Note note = noteDao.getNoteById(id);
        if (note != null) {
            noteDao.softDeleteNote(id);
        } else {
            throw new RuntimeException("Note not found");
        }
    }
}
