package com.chalkim.orinote.service.impl;

import java.time.Instant;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.chalkim.orinote.dto.note.CreateNoteDto;
import com.chalkim.orinote.dto.note.UpdateNoteDto;
import com.chalkim.orinote.exception.ResourceConflictException;
import com.chalkim.orinote.exception.ResourceNotFoundException;
import com.chalkim.orinote.mapper.NoteMapper;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.model.User;
import com.chalkim.orinote.repository.NoteRepository;
import com.chalkim.orinote.service.NoteService;

import lombok.RequiredArgsConstructor;

@Validated
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Transactional
    public Note createNote(@Valid @NotNull CreateNoteDto dto, User user) {
        Note note = noteMapper.toEntity(dto);
        note.setUser(user);
        return noteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public Note getNoteById(@NotNull Long id, User user) {
        return noteRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Note with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Note> getAllNotes(User user) {
        return noteRepository.findAllByUserAndIsDeletedFalseOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Note> searchNotes(Instant from, Instant to, Integer type, String keyword, User user) {
        return noteRepository.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.equal(root.get("user"), user));
            predicates = cb.and(predicates, cb.isFalse(root.get("isDeleted")));

            if (from != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (to != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }
            if (type != null) {
                predicates = cb.and(predicates, cb.equal(root.get("type"), type));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(root.get("title"), pattern),
                        cb.like(root.get("head"), pattern),
                        cb.like(root.get("body"), pattern),
                        cb.like(root.get("tail"), pattern)
                ));
            }
            query.orderBy(cb.desc(root.get("createdAt")));
            return predicates;
        });
    }

    @Transactional
    public Note updateNote(@NotNull Long id, @Valid @NotNull UpdateNoteDto dto, User user) {
        Note note = getNoteById(id, user);

        if (note.getArchivedAt() != null) {
            throw new ResourceConflictException("Cannot update an archived note");
        }

        note.setTitle(dto.getTitle());
        note.setType(dto.getType());
        note.setHead(dto.getHead());
        note.setBody(dto.getBody());
        note.setTail(dto.getTail());
        note.setSummary(dto.getSummary());
        System.out.println(note);
        return noteRepository.save(note);
    }

    @Transactional
    public Note archiveNote(@NotNull Long id, User user) {
        Note note = getNoteById(id, user);
        if (note.getArchivedAt() == null) {
            note.setArchivedAt(java.time.Instant.now());
            return noteRepository.save(note);
        }
        return note;
    }

    @Transactional
    public Note summarizeNote(@NotNull Long id, User user) {
        Note note = getNoteById(id, user);
        if (note.getArchivedAt() != null) {
            throw new ResourceConflictException("Cannot summarize an archived note");
        }
        String prompt = "Generate a summary for the following text: " + note.getBody();
        // Mock API call (to be replaced with Spring AI)
        String analysisResult = "[MOCK] Analysis result for: " + prompt;
        note.setSummary(analysisResult);
        return noteRepository.save(note);
    }

    @Transactional
    public void softDeleteNote(@NotNull Long id, User user) {
        Note note = getNoteById(id, user);
        if (!note.getIsDeleted()) {
            note.setIsDeleted(true);
            noteRepository.save(note);
        }
    }
}
