package com.chalkim.orinote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.chalkim.orinote.service.impl.OAuth2UserServiceImpl;

@Configuration
public class SecurityConfig {

    private final OAuth2UserServiceImpl customOAuth2UserService;

    public SecurityConfig(OAuth2UserServiceImpl customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // 自定义登录页（可选）
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) // 自定义用户信息处理
                )
            );
        return http.build();
    }
}
