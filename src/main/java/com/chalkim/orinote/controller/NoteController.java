package com.chalkim.orinote.controller;

import com.chalkim.orinote.dto.SearchNoteDto;
import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import com.chalkim.orinote.dto.NoteDto;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.service.NoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@Validated
@RestController
@RequestMapping("/api/v1/notes")
@Tag(name = "Note API", description = "管理笔记的增删查改接口")
public class NoteController {
    /**
     * 获取当前登录用户的userId（与数据库主键关联）。
     * 支持JwtAuthenticationFilter注入的principal为Long或String类型。
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();;
        return Long.parseLong(principal.toString());
    }

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Operation(summary = "列出所有笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回笔记列表", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Note.class)))),
    })
    @GetMapping
    public List<Note> getAllNotes() {
        return noteService.getAllNotes(getCurrentUserId());
    }

    @Operation(summary = "创建一个笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "成功创建笔记", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Note.class))),
            @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    public ResponseEntity<Note> saveNote(@RequestBody @Valid NoteDto dto) {
        Note saved = noteService.saveNote(dto, getCurrentUserId());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @Operation(summary = "根据ID获取笔记", description = "通过笔记ID获取笔记详情")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回笔记详情", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Note.class))),
            @ApiResponse(responseCode = "404", description = "未找到指定ID的笔记", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        Note note = noteService.getNoteById(id, getCurrentUserId());
        return ResponseEntity.ok(note);
    }

    @Operation(summary = "根据ID更新笔记", description = "更新指定ID的笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "成功更新笔记"),
            @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "未找到指定ID的笔记", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNote(
            @PathVariable("id") Long id,
            @RequestBody @Valid NoteDto dto) {
        noteService.updateNote(id, getCurrentUserId(), dto);
    }

    @Operation(summary = "根据ID分析笔记", description = "分析指定ID的笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "成功分析笔记"),
            @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "未找到指定ID的笔记", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}/analyse")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Note> analyseNote(
            @PathVariable("id") Long id) {
        Note analysedNote = noteService.analyseNote(id, getCurrentUserId());
        return ResponseEntity.ok(analysedNote);
    }

    @Operation(summary = "根据ID归档笔记", description = "归档指定ID的笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "成功归档笔记"),
            @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "未找到指定ID的笔记", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Note> archiveNote(
            @PathVariable("id") Long id) {
        Note archivedNote = noteService.archiveNote(id, getCurrentUserId());
        return ResponseEntity.ok(archivedNote);
    }

    @Operation(summary = "根据ID删除笔记", description = "软删除指定ID的笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "成功删除笔记"),
            @ApiResponse(responseCode = "404", description = "未找到指定ID的笔记", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    public void softDeleteNote(@PathVariable("id") Long id) {
        noteService.softDeleteNote(id, getCurrentUserId());
    }

    @Operation(summary = "获取指定时间范围内的笔记", description = "返回指定时间范围内的笔记列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回笔记列表", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Note.class))),
            @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/search")
    public List<Note> searchNotes(@RequestBody @Valid SearchNoteDto searchDto) {
        return noteService.searchNotes(getCurrentUserId(), searchDto);
    }
}
