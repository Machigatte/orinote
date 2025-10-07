package com.chalkim.orinote.service.impl;

import com.chalkim.orinote.dto.SearchNoteDto;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.chalkim.orinote.dao.NoteDao;
import com.chalkim.orinote.dto.NoteDto;
import com.chalkim.orinote.exception.ArchivedNoteException;
import com.chalkim.orinote.exception.NoteNotFoundException;
import com.chalkim.orinote.mapper.NoteMapper;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.service.NoteService;

@Validated
@Service
public class NoteServiceImpl implements NoteService {
    private final NoteDao noteDao;
    private final NoteMapper noteMapper;

    public NoteServiceImpl(NoteDao noteDao, NoteMapper noteMapper) {
        this.noteDao = noteDao;
        this.noteMapper = noteMapper;
    }

    @Override
    @Transactional
    public Note saveNote(@Valid @NotNull NoteDto dto, @NotNull Long userId) {
        return noteDao.createNote(dto, userId);
    }

    @Override
    public Note getNoteById(@NotNull Long id, @NotNull Long userId) {
        try {
            return noteDao.getNoteById(id, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
    }

    @Override
    public List<Note> getAllNotes(@NotNull Long userId) {
        return noteDao.getAllNotes(userId);
    }

    @Override
    public List<Note> searchNotes(@NotNull Long userId, SearchNoteDto searchDto) {
        return noteDao.searchNotes(userId, searchDto);
    }


    @Override
    @Transactional
    public void updateNote(@NotNull Long id, @NotNull Long userId, @Valid @NotNull NoteDto dto) {
        boolean exists = noteDao.existsById(id, userId);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }

        boolean isArchived = noteDao.isArchived(id, userId);
        if (isArchived) {
            throw new ArchivedNoteException("Cannot update archived note with ID " + id);
        }

        noteDao.updateNote(id, userId, dto);
    }

    @Override
    @Transactional
    public void archiveNote(@NotNull Long id, @NotNull Long userId, @Valid @NotNull NoteDto dto) {
        boolean exists = noteDao.existsById(id, userId);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
        noteDao.updateArchivedAt(id, userId, dto);
    }

    @Override
    @Transactional
    public Note analyseNote(@NotNull Long id, @NotNull Long userId) {
        try {
            Note note = noteDao.getNoteById(id, userId);
            // Generate prompt based on note type and content

            Boolean isArchived = noteDao.isArchived(id, userId);
            if (isArchived) {
                throw new ArchivedNoteException("Cannot analyse archived note with ID " + id);
            }

            String prompt = "Generate a summary for the following text: " + note.getBody();
            // Mock API call (to be replaced with Spring AI)
            String analysisResult = "[MOCK] Analysis result for: " + prompt;
            note.setSummary(analysisResult);
            noteDao.updateNote(id, userId, noteMapper.noteToNoteDto(note));
            return note;
        } catch (EmptyResultDataAccessException e) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
    }

    @Override
    @Transactional
    public void softDeleteNote(@NotNull Long id, @NotNull Long userId) {
        int rows = noteDao.softDeleteNote(id, userId);
        if (rows == 0) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
    }

    @Override
    @Transactional
    public Note archiveNote(@NotNull Long id, @NotNull Long userId) {
        boolean exists = noteDao.existsById(id, userId);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }

        return noteDao.archiveNote(id, userId);
    }
}
