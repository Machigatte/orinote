package com.chalkim.orinote.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class SummaryCreateDto {
    private String title;
    private String content;
    private Instant startAt;
    private Instant endAt;
}
