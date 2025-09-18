package com.chalkim.orinote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BindGitHubDto {
    @NotBlank
    @Schema(description = "GitHub用户ID", example = "12345678")
    private String providerUserId;
}