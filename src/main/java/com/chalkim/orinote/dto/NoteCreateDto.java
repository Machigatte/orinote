package com.chalkim.orinote.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoteCreateDto {
    @NotBlank
    private String title;
    private String content;
}
