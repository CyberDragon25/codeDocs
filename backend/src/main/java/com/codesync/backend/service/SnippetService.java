package com.codesync.backend.service;

import com.codesync.backend.dto.SnippetRequest;
import com.codesync.backend.dto.SnippetResponse;
import com.codesync.backend.model.Snippet;
import com.codesync.backend.repository.SnippetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer — all business logic lives here, NOT in the controller.
 *
 * @Service     — marks this as a Spring-managed bean
 * @Slf4j       — Lombok: gives you a `log` variable for logging
 * @RequiredArgsConstructor — Lombok: generates a constructor for all
 *               `final` fields, enabling constructor injection (best practice)
 * @Transactional — ensures DB operations are wrapped in a transaction
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SnippetService {

    private final SnippetRepository snippetRepository;

    /**
     * Create a new snippet.
     * Generates a short random share token for the public URL.
     */
    @Transactional
    public SnippetResponse createSnippet(SnippetRequest request, String ownerId) {
        log.debug("Creating snippet for owner: {}", ownerId);

        Snippet snippet = Snippet.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .language(request.getLanguage())
                .ownerId(ownerId)
                .isPublic(request.isPublic())
                .shareToken(generateShareToken())
                .build();

        Snippet saved = snippetRepository.save(snippet);
        log.info("Created snippet with id: {}", saved.getId());
        return toResponse(saved);
    }

    /**
     * Fetch a single snippet by ID.
     * Throws 404-friendly exception if not found.
     */
    @Transactional(readOnly = true)
    public SnippetResponse getSnippet(UUID id) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Snippet not found: " + id));
        return toResponse(snippet);
    }

    /**
     * Fetch a snippet by its public share token.
     * This is what happens when someone opens a share link.
     */
    @Transactional(readOnly = true)
    public SnippetResponse getSnippetByShareToken(String token) {
        Snippet snippet = snippetRepository.findByShareToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Snippet not found for token: " + token));
        return toResponse(snippet);
    }

    /**
     * Get all snippets owned by a user.
     */
    @Transactional(readOnly = true)
    public List<SnippetResponse> getSnippetsByOwner(String ownerId) {
        return snippetRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update snippet content (title, content, language, visibility).
     * In Week 2, real-time edits will go through WebSocket instead.
     */
    @Transactional
    public SnippetResponse updateSnippet(UUID id, SnippetRequest request, String ownerId) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Snippet not found: " + id));

        // Authorization check — only the owner can update
        if (!snippet.getOwnerId().equals(ownerId)) {
            throw new SecurityException("You don't have permission to update this snippet");
        }

        snippet.setTitle(request.getTitle());
        snippet.setContent(request.getContent());
        snippet.setLanguage(request.getLanguage());
        snippet.setPublic(request.isPublic());

        return toResponse(snippetRepository.save(snippet));
    }

    /**
     * Delete a snippet. Owner only.
     */
    @Transactional
    public void deleteSnippet(UUID id, String ownerId) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Snippet not found: " + id));

        if (!snippet.getOwnerId().equals(ownerId)) {
            throw new SecurityException("You don't have permission to delete this snippet");
        }

        snippetRepository.delete(snippet);
        log.info("Deleted snippet: {}", id);
    }

    // -------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------

    /**
     * Map a Snippet entity to a SnippetResponse DTO.
     * Keeps entity internals out of the API response.
     */
    private SnippetResponse toResponse(Snippet snippet) {
        return SnippetResponse.builder()
                .id(snippet.getId())
                .title(snippet.getTitle())
                .content(snippet.getContent())
                .language(snippet.getLanguage())
                .ownerId(snippet.getOwnerId())
                .isPublic(snippet.isPublic())
                .shareToken(snippet.getShareToken())
                .createdAt(snippet.getCreatedAt())
                .updatedAt(snippet.getUpdatedAt())
                .build();
    }

    /**
     * Generates a short random token like "xK3pQ2mR".
     * Used for shareable URLs: /s/xK3pQ2mR
     */
    private String generateShareToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}