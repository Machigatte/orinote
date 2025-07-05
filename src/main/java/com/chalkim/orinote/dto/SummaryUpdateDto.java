package com.chalkim.orinote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SummaryUpdateDto {
    @NotBlank
    @Schema(
        description = "总结的标题，用于标识和描述该总结。",
        example = "2023年10月工作总结"
    )
    private String title;
    
    @Schema(
        description = "总结的内容。",
        example = "这是我的工作总结内容。"
    )
    private String content;
}
