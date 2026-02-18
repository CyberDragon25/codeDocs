package com.codesync.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This is what the SERVER RETURNS to the client.
 * Notice we include shareToken and timestamps here —
 * things the client needs but doesn't set.
 */
@Data
@Builder
public class SnippetResponse {
    private UUID id;
    private String title;
    private String content;
    private String language;
    private String ownerId;
    private boolean isPublic;
    private String shareToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}