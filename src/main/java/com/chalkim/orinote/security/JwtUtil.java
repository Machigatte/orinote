package com.chalkim.orinote.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    // 建议放到配置文件或环境变量
    private static final String SECRET_KEY = "orinote-very-secret-key-should-be-long-and-random-2025";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 1天
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 生成 JWT
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 校验并解析 JWT
    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 判断 token 是否有效
    public static boolean isTokenValid(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
