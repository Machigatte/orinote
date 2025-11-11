package com.chalkim.orinote.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新笔记请求 DTO")
public class UpdateNoteDto{

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
    private Integer type;

    @NotNull
    private String head;
    @NotNull
    private String body;
    @NotNull
    private String tail;
    @NotNull
    private String summary;
}
