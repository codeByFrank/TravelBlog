package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.backend.model.Priority;

/**
 * The data the frontend sends when a wishlist entry is created or changed.
 * It has no id and no userId: the id is created by MongoDB,
 * and the user id always comes from the login, never from the request.
 * The messages are German because the frontend shows them to the user.
 *
 * @param destination    the place or country, must not be empty
 * @param reason         why the user wants to go there, must not be empty
 * @param priority       how much the user wants to go there, must be set
 * @param bestTravelTime when to go there, e.g. "Februar"; optional
 */
public record WishlistEntryRequest(
        @NotBlank(message = "Bitte einen Ort oder ein Land eingeben.")
        String destination,

        @NotBlank(message = "Bitte einen Grund eingeben.")
        String reason,

        @NotNull(message = "Bitte eine Priorität wählen.")
        Priority priority,

        @Size(max = 100, message = "Die Reisezeit darf höchstens 100 Zeichen lang sein.")
        String bestTravelTime
) {
}
