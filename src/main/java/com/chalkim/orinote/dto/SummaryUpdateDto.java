package com.chalkim.orinote.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SummaryUpdateDto {
    @NotBlank
    private String title;
    
    private String content;
}
