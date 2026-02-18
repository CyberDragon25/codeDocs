package com.codesync.backend.repository;

import com.codesync.backend.model.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Snippets.
 *
 * By extending JpaRepository, Spring AUTO-GENERATES all of these for free:
 *   save(), findById(), findAll(), deleteById(), count(), existsById()
 *
 * You just declare method signatures below and Spring figures out the SQL.
 * This is called "derived query methods" — one of Spring's best features.
 */
@Repository
public interface SnippetRepository extends JpaRepository<Snippet, UUID> {

    // SELECT * FROM snippets WHERE owner_id = ?
    List<Snippet> findByOwnerId(String ownerId);

    // SELECT * FROM snippets WHERE share_token = ?
    Optional<Snippet> findByShareToken(String shareToken);

    // SELECT * FROM snippets WHERE owner_id = ? AND is_public = ?
    List<Snippet> findByOwnerIdAndIsPublic(String ownerId, boolean isPublic);

    // SELECT COUNT(*) FROM snippets WHERE owner_id = ?
    long countByOwnerId(String ownerId);
}