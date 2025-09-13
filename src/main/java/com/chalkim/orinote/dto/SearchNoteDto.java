package com.chalkim.orinote.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

public class SearchNoteDto {
    @Schema(description = "起始时间", example = "2023-01-01T00:00:00Z")
    private Instant from;
    
    @Schema(description = "结束时间", example = "2023-12-31T23:59:59Z")
    private Instant to;
    
    @Schema(description = "笔记类型(1=周报, 2=科研日记)", example = "1")
    private Integer noteType;
    
    @Schema(description = "搜索关键字", example = "项目进展")
    private String keyword;

    // Getters and Setters
    public Instant getFrom() { return from; }
    public void setFrom(Instant from) { this.from = from; }
    public Instant getTo() { return to; }
    public void setTo(Instant to) { this.to = to; }
    public Integer getNoteType() { return noteType; }
    public void setNoteType(Integer noteType) { this.noteType = noteType; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}