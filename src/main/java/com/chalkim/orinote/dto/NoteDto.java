package com.chalkim.orinote.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoteDto {
    @NotBlank
    @Schema(
        description = "笔记的标题，用于标识和描述该笔记。",
        example = "我的第一篇笔记"
    )
    private String title;

    @NotNull
    @Schema(
        description = "笔记的类型，表示笔记的分类或格式。1=周报, 2=科研日记",
        example = "1"
    )
    private Integer noteType;
    
    private String head;
    private String body;
    private String tail;
    private String summary;
    private Instant archivedAt;
}
