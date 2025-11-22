package com.chalkim.orinote.controller;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.chalkim.orinote.dto.note.CreateNoteDto;
import com.chalkim.orinote.dto.note.NoteDetailDto;
import com.chalkim.orinote.dto.note.NoteListDto;
import com.chalkim.orinote.dto.note.UpdateNoteDto;
import com.chalkim.orinote.mapper.NoteMapper;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.model.User;
import com.chalkim.orinote.service.NoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Validated
@RestController
@RequestMapping("/api/notes")
@Tag(name = "Note API", description = "管理笔记的增删查改接口")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private final NoteMapper noteMapper;

    @Operation(summary = "查询笔记（可搜索）")
    @GetMapping
    public ResponseEntity<List<NoteListDto>> listNotes(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal User user) {
        List<Note> notes;

        if (from == null && to == null && type == null && keyword == null) {
            notes = noteService.getAllNotes(user);
        } else {
            notes = noteService.searchNotes(from, to, type, keyword, user);
        }

        return ResponseEntity.ok(noteMapper.toDtoList(notes));
    }

    @Operation(summary = "创建一个笔记")
    @PostMapping
    public ResponseEntity<NoteDetailDto> createNote(
            @RequestBody @Valid CreateNoteDto dto,
            @AuthenticationPrincipal User user) {
        Note note = noteService.createNote(dto, user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(note.getId())
                .toUri();
        return ResponseEntity.created(location).body(noteMapper.toDto(note));
    }

    @Operation(summary = "根据ID获取笔记", description = "通过笔记ID获取笔记详情")
    @GetMapping("/{id}")
    public ResponseEntity<NoteDetailDto> getNoteById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {
        Note note = noteService.getNoteById(id, user);
        return ResponseEntity.ok(noteMapper.toDto(note));
    }

    @Operation(summary = "根据ID更新笔记", description = "更新指定ID的笔记")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<NoteDetailDto> updateNote(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateNoteDto dto,
            @AuthenticationPrincipal User user) {
        Note note = noteService.updateNote(id, dto, user);
        return ResponseEntity.ok(noteMapper.toDto(note));
    }

    @Operation(summary = "根据ID总结笔记", description = "总结指定ID的笔记")
    @PutMapping("/{id}/summarize")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<NoteDetailDto> summarizeNote(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {
        Note note = noteService.summarizeNote(id, user);
        return ResponseEntity.ok(noteMapper.toDto(note));
    }

    /**
     * 调用服务层的 summarizeNoteStream 方法，并将 Flux<String> 返回给客户端。
     * Produces = MediaType.TEXT_EVENT_STREAM_VALUE 表示返回 SSE 流。
     */
    @GetMapping(value = "/{id}/summarize-stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> summarizeNoteStream(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return noteService.summarizeNoteStream(id, user);
    }

    @Operation(summary = "根据ID归档笔记", description = "归档指定ID的笔记")
    @PutMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<NoteDetailDto> archiveNote(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {
        Note note = noteService.archiveNote(id, user);
        return ResponseEntity.ok(noteMapper.toDto(note));
    }

    @Operation(summary = "根据ID删除笔记", description = "软删除指定ID的笔记")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteNote(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {
        noteService.softDeleteNote(id, user);
        return ResponseEntity.noContent().build();
    }
}
