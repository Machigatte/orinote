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

        boolean isArchived = noteDao.isArchived(id);
        if (isArchived) {
            throw new ArchivedNoteException("Cannot update archived note with ID " + id);
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
    public Note analyseNote(@NotNull Long id) {
        try {
            Note note = noteDao.getNoteById(id);
            // Generate prompt based on note type and content

            Boolean isArchived = noteDao.isArchived(id);
            if (isArchived) {
                throw new ArchivedNoteException("Cannot analyse archived note with ID " + id);
            }

            String prompt = "Generate a summary for the following text: " + note.getBody();
            // Mock API call (to be replaced with Spring AI)
            String analysisResult = "[MOCK] Analysis result for: " + prompt;
            note.setSummary(analysisResult);
            noteDao.updateNote(id, noteMapper.noteToNoteDto(note));
            return note;
        } catch (EmptyResultDataAccessException e) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }
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
    public Note archiveNote(@NotNull Long id) {
        boolean exists = noteDao.existsById(id);
        if (!exists) {
            throw new NoteNotFoundException("Note with ID " + id + " not found");
        }

        return noteDao.archiveNote(id);
    }
}
