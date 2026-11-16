package org.example.backend.repository;

import org.example.backend.model.WishlistEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Database access for wishlist entries.
 * Spring Data creates the implementation of this interface automatically.
 * The method names tell Spring Data which query to run.
 */
@Repository
public interface WishlistRepo extends MongoRepository<WishlistEntry, String> {

    /**
     * Finds all wishlist entries of one user.
     */
    List<WishlistEntry> findAllByUserId(String userId);

    /**
     * Finds one wishlist entry, but only if it belongs to the given user.
     * Returns an empty Optional when the entry does not exist or belongs to another user.
     */
    Optional<WishlistEntry> findByIdAndUserId(String id, String userId);
}
