package com.chalkim.orinote.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SummaryCreateDto {
    private String title;
    private String content;
    private Instant startAt;
    private Instant endAt;
}
