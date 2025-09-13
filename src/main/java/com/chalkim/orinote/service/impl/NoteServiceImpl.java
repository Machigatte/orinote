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
    public void archiveNote(@NotNull Long id,  @Valid @NotNull NoteDto dto) {
        boolean exists = noteDao.existsById(id);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
        noteDao.updateArchivedAt(id, dto);
    }


    @Override
    @Transactional
    public void analyseNote(@NotNull Long id) {
        boolean exists = noteDao.existsById(id);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }

        noteDao.analyseNote(id);
    }

    @Override
    @Transactional
    public void softDeleteNote(@NotNull Long id) {
        int rows = noteDao.softDeleteNote(id);
        if (rows == 0) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
    }

    @Override
    @Transactional

    public void archiveNote(@NotNull Long id) {
        boolean exists = noteDao.existsById(id);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }

        noteDao.archiveNote(id);
    }

    @Override
    @Transactional
    public void analyseNote(@NotNull Long id,  @Valid @NotNull NoteDto dto) {
        boolean exists = noteDao.existsById(id);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }

        // Generate prompt based on note type and content
        String prompt = "Generate a summary for the following text: " + dto.getBody();
        // Mock API call (to be replaced with Spring AI)
        String analysisResult = "[MOCK] Analysis result for: " + prompt;
        dto.setSummary(analysisResult);

        noteDao.analyseNote(id, dto);
    }
}
