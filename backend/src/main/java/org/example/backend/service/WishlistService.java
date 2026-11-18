package org.example.backend.service;

import org.example.backend.dto.WishlistEntryRequest;
import org.example.backend.dto.WishlistEntryResponse;
import org.example.backend.model.WishlistEntry;
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
    public List<WishlistEntryResponse> getAllEntries(String userId) {
        return wishlistRepo.findAllByUserId(userId).stream()
                .map(WishlistService::toResponse)
                .toList();
    }

    /**
     * Creates a new wishlist entry for the given user.
     * The id is null here; MongoDB creates it when the entry is saved.
     */
    public WishlistEntryResponse addEntry(String userId, WishlistEntryRequest request) {
        WishlistEntry newEntry = new WishlistEntry(
                null,
                userId,
                request.destination(),
                request.reason(),
                request.priority(),
                emptyToNull(request.bestTravelTime()),
                cleanCountryCodes(request.countryCodes())
        );

        WishlistEntry savedEntry = wishlistRepo.save(newEntry);
        return toResponse(savedEntry);
    }

    /**
     * Changes an existing wishlist entry of the given user.
     *
     * @throws NoSuchElementException when the entry does not exist or belongs to another user
     */
    public WishlistEntryResponse updateEntry(String userId, String id, WishlistEntryRequest request) {
        WishlistEntry existingEntry = findEntryOfUser(userId, id);

        WishlistEntry updatedEntry = existingEntry
                .withDestination(request.destination())
                .withReason(request.reason())
                .withPriority(request.priority())
                .withBestTravelTime(emptyToNull(request.bestTravelTime()))
                .withCountryCodes(cleanCountryCodes(request.countryCodes()));

        WishlistEntry savedEntry = wishlistRepo.save(updatedEntry);
        return toResponse(savedEntry);
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

    /**
     * Turns the stored document into the answer for the client.
     * The userId is left out on purpose: the owner id is not sent to the client.
     */
    private static WishlistEntryResponse toResponse(WishlistEntry entry) {
        return new WishlistEntryResponse(
                entry.id(),
                entry.destination(),
                entry.reason(),
                entry.priority(),
                entry.bestTravelTime(),
                cleanCountryCodes(entry.countryCodes())
        );
    }

    /**
     * A code that appears twice is kept once.
     * Entries stored before countries existed have no list at all (null); they are answered with an empty list.
     * (New requests always have at least one country; WishlistEntryRequest checks that.)
     */
    private static List<String> cleanCountryCodes(List<String> countryCodes) {
        if (countryCodes == null) {
            return List.of();
        }
        return countryCodes.stream()
                .distinct()
                .toList();
    }

    /**
     * The best travel time is optional: an empty or blank text is stored as "no value" (null).
     */
    private static String emptyToNull(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.trim();
    }
}
