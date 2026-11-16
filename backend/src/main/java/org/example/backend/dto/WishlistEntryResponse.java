package org.example.backend.dto;

import org.example.backend.model.Priority;

/**
 * A wishlist entry as the API returns it.
 * It has no userId: the owner id stays on the server.
 *
 * @param id             unique id of the entry
 * @param destination    the place or country
 * @param reason         why the user wants to go there
 * @param priority       how much the user wants to go there
 * @param bestTravelTime when to go there, e.g. "Februar"; null when the user left it empty
 */
public record WishlistEntryResponse(
        String id,
        String destination,
        String reason,
        Priority priority,
        String bestTravelTime
) {
}
