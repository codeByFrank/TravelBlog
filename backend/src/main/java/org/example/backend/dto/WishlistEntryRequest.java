package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.backend.model.Priority;

/**
 * The data the frontend sends when a wishlist entry is created or changed.
 * It has no id and no userId: the id is created by MongoDB,
 * and the user id always comes from the login, never from the request.
 *
 * @param destination the place or country, must not be empty
 * @param reason      why the user wants to go there, must not be empty
 * @param priority    how much the user wants to go there, must be set
 */
public record WishlistEntryRequest(
        @NotBlank String destination,
        @NotBlank String reason,
        @NotNull Priority priority
) {
}
