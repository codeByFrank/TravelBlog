package org.example.backend.service;

import org.example.backend.model.WishlistEntry;
import org.example.backend.dto.WishlistEntryRequest;
import org.example.backend.repository.WishlistRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * The logic of the wishlist.
 * Every method gets the id of the logged-in user (userId),
 * so a user can only see and change their own entries.
 */
@Service
public class WishlistService {

    private final WishlistRepo wishlistRepo;

    public WishlistService(WishlistRepo wishlistRepo) {
        this.wishlistRepo = wishlistRepo;
    }

    /**
     * Returns all wishlist entries of one user.
     */
    public List<WishlistEntry> getAllEntries(String userId) {
        return wishlistRepo.findAllByUserId(userId);
    }

    /**
     * Creates a new wishlist entry for the given user.
     * The id is null here; MongoDB creates it when the entry is saved.
     */
    public WishlistEntry addEntry(String userId, WishlistEntryRequest request) {
        WishlistEntry newEntry = new WishlistEntry(
                null,
                userId,
                request.destination(),
                request.reason(),
                request.priority()
        );

        return wishlistRepo.save(newEntry);
    }

    /**
     * Changes an existing wishlist entry of the given user.
     *
     * @throws NoSuchElementException when the entry does not exist or belongs to another user
     */
    public WishlistEntry updateEntry(String userId, String id, WishlistEntryRequest request) {
        WishlistEntry existingEntry = findEntryOfUser(userId, id);

        WishlistEntry updatedEntry = existingEntry
                .withDestination(request.destination())
                .withReason(request.reason())
                .withPriority(request.priority());

        return wishlistRepo.save(updatedEntry);
    }

    /**
     * Deletes a wishlist entry of the given user.
     *
     * @throws NoSuchElementException when the entry does not exist or belongs to another user
     */
    public void deleteEntry(String userId, String id) {
        WishlistEntry existingEntry = findEntryOfUser(userId, id);

        wishlistRepo.delete(existingEntry);
    }

    /**
     * Looks up one entry and makes sure it belongs to the given user.
     * A missing entry and an entry of another user are treated the same way,
     * so nobody can find out which ids exist.
     */
    private WishlistEntry findEntryOfUser(String userId, String id) {
        return wishlistRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Wishlist entry not found: " + id));
    }
}
