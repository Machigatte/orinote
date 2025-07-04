package com.chalkim.orinote.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SummaryCreateDto {
    @NotBlank
    private String title;
    private String content;

    @NotNull
    private Instant startAt;
    @NotNull
    private Instant endAt;
}
