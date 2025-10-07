package com.chalkim.orinote.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 1天

    public String generateToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String subject;
        if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
            subject = oidcUser.getSubject();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oauth2User) {
            // GitHub 用户名通常在 login 属性
            subject = oauth2User.getAttribute("login");
            if (subject == null) {
                subject = oauth2User.getName();
            }
        } else {
            subject = authentication.getName();
        }
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setSubject(subject)
                .claim("authorities", authorities)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
