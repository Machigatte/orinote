package com.chalkim.orinote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.chalkim.orinote.security.OAuth2UserServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final OAuth2UserServiceImpl oAuth2UserService;

    public SecurityConfig(OAuth2UserServiceImpl oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/actuator/health", "/error", "/oauth2/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2UserService)
                )
                .failureHandler(oauth2AuthenticationFailureHandler())
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            )
            // Disable CSRF for API endpoints (can be configured more specifically if needed)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/admin/**")
            );

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler oauth2AuthenticationFailureHandler() {
        return (request, response, exception) -> {
            log.error("OAuth2 authentication failed: {}", exception.getMessage());
            
            String errorCode = "authentication_failed";
            String errorMessage = "Authentication failed";
            
            if (exception.getCause() != null && 
                exception.getCause().getMessage() != null) {
                String causeMessage = exception.getCause().getMessage();
                if (causeMessage.contains("unauthorized_github_user")) {
                    errorCode = "unauthorized_github_user";
                    errorMessage = "Your GitHub account is not authorized";
                } else if (causeMessage.contains("account_disabled")) {
                    errorCode = "account_disabled";
                    errorMessage = "Your account has been disabled";
                }
            }
            
            // Redirect to login page with error parameter
            response.sendRedirect("/login?error=" + errorCode + "&message=" + errorMessage);
        };
    }
}