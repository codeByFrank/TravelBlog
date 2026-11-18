package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.backend.model.Priority;

import java.util.List;

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
 * @param countryCodes   the countries of the destination, e.g. ["AR", "CL"]; at least 1, at most 10;
 *                       each code is two capital letters (ISO 3166-1 alpha-2)
 */
public record WishlistEntryRequest(
        @NotBlank(message = "Bitte einen Ort oder ein Land eingeben.")
        String destination,

        @NotBlank(message = "Bitte einen Grund eingeben.")
        String reason,

        @NotNull(message = "Bitte eine Priorität wählen.")
        Priority priority,

        @Size(max = 100, message = "Die Reisezeit darf höchstens 100 Zeichen lang sein.")
        String bestTravelTime,

        // The rule inside the brackets is checked for every single code in the list
        @NotEmpty(message = "Bitte mindestens ein Land wählen.")
        @Size(max = 10, message = "Bitte höchstens 10 Länder wählen.")
        List<@Pattern(regexp = "^[A-Z]{2}$", message = "Ungültiger Ländercode.") String> countryCodes
) {
}
