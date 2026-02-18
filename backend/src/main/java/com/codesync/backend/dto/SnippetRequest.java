package com.codesync.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO = Data Transfer Object.
 *
 * This is what the CLIENT SENDS when creating/updating a snippet.
 * We never expose our @Entity (model) directly to the API — DTOs
 * act as a clean contract between frontend and backend, and protect
 * against over-posting attacks (client setting fields they shouldn't).
 */
@Data
public class SnippetRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be under 255 characters")
    private String title;

    private String content;

    @NotBlank(message = "Language is required")
    private String language;

    private boolean isPublic = true;
}