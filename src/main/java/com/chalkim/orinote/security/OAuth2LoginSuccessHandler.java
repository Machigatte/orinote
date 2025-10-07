package com.chalkim.orinote.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.chalkim.orinote.service.OAuth2AccountService;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private OAuth2AccountService oAuth2AccountService;

    public OAuth2LoginSuccessHandler(OAuth2AccountService oAuth2AccountService) {
        this.oAuth2AccountService = oAuth2AccountService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String idFromProvider = authentication.getName();
        String jwt = oAuth2AccountService.loginOrRegister(provider, idFromProvider, null);
        String redirectUrl = "http://localhost:3000/login/callback?jwt=" + jwt;
        response.sendRedirect(redirectUrl);
    }
}
