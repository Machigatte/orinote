package com.chalkim.orinote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank
    @Schema(description = "用户名", example = "john_doe")
    private String username;

    @Email
    @Schema(description = "邮箱地址", example = "john@example.com")
    private String email;

    @Schema(description = "用户角色", example = "ROLE_USER", allowableValues = {"ROLE_USER", "ROLE_ADMIN"})
    private String role = "ROLE_USER";

    @Schema(description = "账号是否启用", example = "true")
    private Boolean enabled = true;
}