package com.chalkim.orinote.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        String jwt = JwtUtil.generateToken(username);
        // 登录成功后重定向到前端页面并带上 JWT
        String redirectUrl = "http://localhost:3000/login/callback?jwt=" + jwt;
        System.out.println("Redirecting to: " + redirectUrl); // Debug log
        response.sendRedirect(redirectUrl);
    }
}
