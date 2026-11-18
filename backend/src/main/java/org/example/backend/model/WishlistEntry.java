package org.example.backend.model;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * A place or country the user still wants to visit.
 * One WishlistEntry is stored as one document in the MongoDB collection "wishlist".
 *
 * @param id             unique id of the entry, created by MongoDB when the entry is saved for the first time
 * @param userId         id of the user this entry belongs to
 * @param destination    the place or country, e.g. "Island"
 * @param reason         why the user wants to go there, e.g. "Nordlichter sehen"
 * @param priority       how much the user wants to go there
 * @param bestTravelTime when to go there, e.g. "Februar"; null when the user left it empty
 * @param countryCodes   the countries of the destination as ISO 3166-1 alpha-2 codes, e.g. ["AR", "CL"];
 *                       null in entries stored before this field existed
 */
@Document(collection = "wishlist")
@With
public record WishlistEntry(
        @Id String id,
        String userId,
        String destination,
        String reason,
        Priority priority,
        String bestTravelTime,
        List<String> countryCodes
) {
}
