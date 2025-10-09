package com.chalkim.orinote.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chalkim.orinote.dao.UserDao;
import com.chalkim.orinote.security.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Auth API", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDao userDao;

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
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            // 登录成功，生成 JWT
            String userId = userDao.findByUsername(username).get().getId().toString();
            String token = jwtUtil.generateToken(userId);
            return Collections.singletonMap("token", token);
        } catch (AuthenticationException e) {
            // 登录失败
            return Collections.singletonMap("error", "Invalid username or password");
        }
    }
}
