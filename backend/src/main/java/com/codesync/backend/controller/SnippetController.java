package com.codesync.backend.controller;

import com.codesync.backend.dto.SnippetRequest;
import com.codesync.backend.dto.SnippetResponse;
import com.codesync.backend.service.SnippetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for snippet CRUD operations.
 *
 * @RestController  — marks this as a controller where every method returns
 *                   JSON (combines @Controller + @ResponseBody)
 * @RequestMapping  — base path for all routes in this controller
 * @CrossOrigin     — allows your React/Svelte frontend (on a different port)
 *                   to call this API during development
 *
 * NOTE: The `ownerId` parameter is hardcoded as a request header for now.
 * In Week 2 we'll replace this with JWT authentication — the user identity
 * will come from the token automatically.
 */
@RestController
@RequestMapping("/api/v1/snippets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // tighten this in production
public class SnippetController {

    private final SnippetService snippetService;

    /**
     * POST /api/v1/snippets
     * Create a new snippet.
     *
     * @Valid triggers validation annotations on SnippetRequest (e.g. @NotBlank)
     * @RequestHeader("X-User-Id") — temporary stand-in for real auth
     */
    @PostMapping
    public ResponseEntity<SnippetResponse> createSnippet(
            @Valid @RequestBody SnippetRequest request,
            @RequestHeader("X-User-Id") String ownerId) {

        SnippetResponse response = snippetService.createSnippet(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/snippets/{id}
     * Get a snippet by its UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SnippetResponse> getSnippet(@PathVariable UUID id) {
        return ResponseEntity.ok(snippetService.getSnippet(id));
    }

    /**
     * GET /api/v1/snippets/share/{token}
     * Get a snippet by its public share token (used for share links).
     * e.g. GET /api/v1/snippets/share/xK3pQ2mR
     */
    @GetMapping("/share/{token}")
    public ResponseEntity<SnippetResponse> getSnippetByToken(@PathVariable String token) {
        return ResponseEntity.ok(snippetService.getSnippetByShareToken(token));
    }

    /**
     * GET /api/v1/snippets/my
     * Get all snippets for the authenticated user.
     */
    @GetMapping("/my")
    public ResponseEntity<List<SnippetResponse>> getMySnippets(
            @RequestHeader("X-User-Id") String ownerId) {
        return ResponseEntity.ok(snippetService.getSnippetsByOwner(ownerId));
    }

    /**
     * PUT /api/v1/snippets/{id}
     * Update a snippet (full update).
     */
    @PutMapping("/{id}")
    public ResponseEntity<SnippetResponse> updateSnippet(
            @PathVariable UUID id,
            @Valid @RequestBody SnippetRequest request,
            @RequestHeader("X-User-Id") String ownerId) {

        return ResponseEntity.ok(snippetService.updateSnippet(id, request, ownerId));
    }

    /**
     * DELETE /api/v1/snippets/{id}
     * Delete a snippet.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnippet(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String ownerId) {

        snippetService.deleteSnippet(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}