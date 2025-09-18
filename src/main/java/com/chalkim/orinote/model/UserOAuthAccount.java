package com.chalkim.orinote.model;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserOAuthAccount {
    private Long id;
    private Long userId;
    private String provider;
    private String providerUserId;
    private Instant createdAt;
    private Instant updatedAt;
}