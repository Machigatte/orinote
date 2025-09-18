package com.chalkim.orinote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfoDto {
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "用户角色")
    private String role;

    @Schema(description = "账号是否启用")
    private Boolean enabled;
}