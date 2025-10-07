package com.chalkim.orinote.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.chalkim.orinote.security.JwtUtil;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "User API", description = "用户相关接口")
@RestController
@RequestMapping("/api")
public class UserController {

    @Operation(summary = "获取当前用户信息", description = "需要携带有效 JWT token。返回当前登录用户的用户名。")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功返回用户信息", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "未认证或token无效")
    })
    @GetMapping("/user")
    public Map<String, Object> getCurrentUser(@Parameter(hidden = true) Principal principal) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", principal.getName());
        return userInfo;
    }

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
        summary = "用户登录，获取 JWT token",
        description = "提交用户名和密码，返回 JWT token。前端需在后续请求中携带 Authorization: Bearer {token}。"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功，返回 token", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"token\": \"xxxxxx\" }"))),
        @ApiResponse(responseCode = "401", description = "登录失败，用户名或密码错误", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"error\": \"Invalid username or password\" }")))
    })
    @PostMapping("/login")
    public Map<String, Object> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "登录请求体，包含用户名和密码",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(value = "{ \"username\": \"test\", \"password\": \"123456\" }")
            )
        )
        @RequestBody Map<String, String> loginRequest
    ) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            // 登录成功，生成 JWT
            String userId = authentication.getName();
            String token = jwtUtil.generateToken(userId);
            return Collections.singletonMap("token", token);
        } catch (AuthenticationException e) {
            // 登录失败
            return Collections.singletonMap("error", "Invalid username or password");
        }
    }
}