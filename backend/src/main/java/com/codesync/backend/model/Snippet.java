package com.codesync.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a code snippet in CodeSync.
 *
 * @Entity  — tells JPA this class maps to a database table
 * @Table   — names the table explicitly (good practice)
 * @Data    — Lombok: generates getters, setters, toString, equals, hashCode
 * @Builder — Lombok: lets you do Snippet.builder().title("foo").build()
 */
@Entity
@Table(name = "snippets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Snippet {

    /**
     * UUID primary key.
     * Better than auto-increment integers for distributed systems
     * because you can generate IDs client-side without DB round-trips.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    /**
     * The display title of the snippet.
     */
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    /**
     * The actual code content.
     * @Lob / columnDefinition = TEXT allows unlimited length.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Programming language for syntax highlighting.
     * Stored as a String (e.g. "javascript", "python").
     * We'll use an enum later but string is simpler to start.
     */
    @Column(nullable = false)
    private String language;

    /**
     * Who owns this snippet.
     * For now just a string (username), we'll FK to a User table later.
     */
    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    /**
     * Public snippets are accessible by anyone with the link.
     * Private snippets require authentication.
     */
    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = true;

    /**
     * A short unique share token for the public URL.
     * e.g. codesync.dev/s/xK3pQ2
     */
    @Column(name = "share_token", unique = true)
    private String shareToken;

    // --- Timestamps (auto-managed by Hibernate) ---

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}