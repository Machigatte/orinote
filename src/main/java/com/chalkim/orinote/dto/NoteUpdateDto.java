package com.chalkim.orinote.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoteUpdateDto {
    @NotBlank
    private String title;
    private String content;
}
