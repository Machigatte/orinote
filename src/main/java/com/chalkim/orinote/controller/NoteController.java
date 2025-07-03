package com.chalkim.orinote.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.service.NoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notes")
@Tag(name = "Note API", description = "管理笔记的增删查改接口")
public class NoteController {
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
    public Note saveNote(@RequestBody Note note) {
        return noteService.saveNote(note);
    }

    // GET /notes/{id} -> get note by id
    @Operation(summary = "根据ID获取笔记")
    @GetMapping("/{id}")
    public Optional<Note> getNotaById(@PathVariable("id") Long id) {
        return noteService.getNoteById(id);
    }

    // POST /notes/{id} -> update note by id
    @Operation(summary = "根据ID更新笔记")
    @PostMapping("/{id}")
    public void updateNote(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content) {
        noteService.updateNote(id, title, content);
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
