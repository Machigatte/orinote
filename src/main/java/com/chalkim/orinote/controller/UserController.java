package com.chalkim.orinote.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chalkim.orinote.dto.UserInfoDto;
import com.chalkim.orinote.security.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/api")
@Tag(name = "User API", description = "用户信息接口")
public class UserController {

    @Operation(summary = "获取当前用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功返回用户信息"),
        @ApiResponse(responseCode = "401", description = "未认证")
    })
    @GetMapping("/me")
    public UserInfoDto getCurrentUser(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        UserInfoDto userInfo = new UserInfoDto();
        userInfo.setId(currentUser.getUserId());
        userInfo.setUsername(currentUser.getName());
        userInfo.setEmail(currentUser.getEmail());
        userInfo.setRole(currentUser.getRole());
        userInfo.setEnabled(currentUser.isEnabled());
        
        return userInfo;
    }
}