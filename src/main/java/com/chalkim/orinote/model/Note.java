package com.chalkim.orinote.model;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Note {
    private Long id;
    private Integer noteType;
    private String title;

    private String head;
    private String body;
    private String tail;
    private String summary;

    private Boolean isDeleted;
    private Instant achievedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
