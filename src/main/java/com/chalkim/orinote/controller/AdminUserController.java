package com.chalkim.orinote.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chalkim.orinote.dto.BindGitHubDto;
import com.chalkim.orinote.dto.UserDto;
import com.chalkim.orinote.model.User;
import com.chalkim.orinote.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin User API", description = "管理员用户管理接口")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "创建新用户")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "成功创建用户"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Admin creating user: username={}", userDto.getUsername());
        return userService.createUser(userDto);
    }

    @Operation(summary = "获取所有用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功返回用户列表"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "根据ID获取用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功返回用户信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Operation(summary = "更新用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功更新用户"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        log.info("Admin updating user: id={}, username={}", id, userDto.getUsername());
        return userService.updateUser(id, userDto);
    }

    @Operation(summary = "禁用用户")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "成功禁用用户"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PatchMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableUser(@PathVariable Long id) {
        log.info("Admin disabling user: id={}", id);
        userService.setUserEnabled(id, false);
    }

    @Operation(summary = "启用用户")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "成功启用用户"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PatchMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enableUser(@PathVariable Long id) {
        log.info("Admin enabling user: id={}", id);
        userService.setUserEnabled(id, true);
    }

    @Operation(summary = "绑定用户的GitHub账号")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "成功绑定GitHub账号"),
        @ApiResponse(responseCode = "400", description = "请求参数无效或GitHub账号已被绑定"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/{id}/bind-github")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bindGitHubAccount(@PathVariable Long id, @Valid @RequestBody BindGitHubDto bindDto) {
        log.info("Admin binding GitHub account: userId={}, providerUserId={}", id, bindDto.getProviderUserId());
        userService.bindGitHubAccount(id, bindDto.getProviderUserId());
    }

    @Operation(summary = "解绑用户的GitHub账号")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "成功解绑GitHub账号"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @DeleteMapping("/{id}/github-binding")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unbindGitHubAccount(@PathVariable Long id) {
        log.info("Admin unbinding GitHub account: userId={}", id);
        userService.unbindGitHubAccount(id);
    }
}