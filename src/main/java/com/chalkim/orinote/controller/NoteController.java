package com.chalkim.orinote.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chalkim.orinote.dto.NoteCreateDto;
import com.chalkim.orinote.dto.NoteUpdateDto;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.service.NoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notes")
@Tag(name = "Note API", description = "管理笔记的增删查改接口")
public class NoteController {
    private static final Logger log = LoggerFactory.getLogger(NoteController.class);

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // GET /notes -> liot all notes
    @Operation(summary = "列出所有笔记")
    @GetMapping
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }

    // POST /notes -> create note
    @Operation(summary = "保存一个笔记")
    @PostMapping
    public Note saveNote(@RequestBody @Validated NoteCreateDto dto) {
        return noteService.saveNote(dto);
    }

    // GET /notes/{id} -> get note by id
    @Operation(summary = "根据ID获取笔记")
    @GetMapping("/{id}")
    public Optional<Note> getNotaById(@PathVariable("id") Long id) {
        return noteService.getNoteById(id);
    }

    // POST /notes/{id} -> update note by id
    @Operation(summary = "根据ID更新笔记")
    @PatchMapping("/{id}")
    public void updateNote(
            @PathVariable("id") Long id,
            @RequestBody @Validated NoteUpdateDto dto) {
        noteService.updateNote(id, dto);
    }

    // DELETE /notes/{id} -> delete note by id
    @Operation(summary = "根据ID删除笔记")
    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable("id") Long id) {
        noteService.softDeleteNote(id);
    }

    // GET /notes/range?from=...&to=... -> get notes in range
    @Operation(summary = "获取指定时间范围内的笔记")
    @GetMapping("/range")
    public List<Note> getNotesBetween(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return noteService.getNotesBetween(from, to);
    }
}
