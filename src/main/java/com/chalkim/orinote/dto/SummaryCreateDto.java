package com.chalkim.orinote.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SummaryCreateDto {
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

    @NotNull
    @Schema(
        description = "总结的起始时间，表示总结覆盖的时间段开始。",
        example = "2025-07-01T00:00:00.000Z"
    )
    private Instant startAt;
    
    @NotNull
    @Schema(
        description = "总结的结束时间，表示总结覆盖的时间段结束。",
        example = "2025-07-31T00:00:00.000Z"
    )
    private Instant endAt;
}
