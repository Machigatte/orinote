package com.chalkim.orinote.model;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private Long id;

    private String username;
    private String passwd;

    private Instant createdAt;
    private Instant updatedAt;
}
