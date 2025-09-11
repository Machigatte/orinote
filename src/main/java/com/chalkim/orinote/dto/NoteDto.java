package com.chalkim.orinote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoteDto {
    @NotBlank
    @Schema(
        description = "笔记的标题，用于标识和描述该笔记。",
        example = "我的第一篇笔记"
    )
    private String title;
    private Integer noteType;
    
    private String head;
    private String body;
    private String tail;
    private String summary;
}
