package com.chalkim.orinote.model;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Note {
    private Long id;
    private String title;
    private String content;
    private Boolean isDeleted;
    private Instant createdAt;
    private Instant updatedAt;
}
