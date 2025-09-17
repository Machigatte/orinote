package com.chalkim.orinote.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.chalkim.orinote.model.User;

import lombok.Getter;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final User localUser;
    private final Map<String, Object> attributes;
    private final Collection<GrantedAuthority> authorities;

    public CustomOAuth2User(User localUser, Map<String, Object> attributes) {
        this.localUser = localUser;
        this.attributes = attributes;
        // Ensure role is not null for SimpleGrantedAuthority
        String role = localUser.getRole() != null ? localUser.getRole() : "ROLE_USER";
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return localUser.getUsername() != null ? localUser.getUsername() : "unknown";
    }

    public Long getUserId() {
        return localUser.getId();
    }

    public String getRole() {
        return localUser.getRole();
    }

    public String getEmail() {
        return localUser.getEmail();
    }

    public boolean isEnabled() {
        return localUser.getEnabled() != null ? localUser.getEnabled() : false;
    }
}