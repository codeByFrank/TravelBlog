package org.example.backend.dto;

/**
 * One result of the place search.
 *
 * @param name        short name, e.g. "Florenz"
 * @param displayName full name to tell places apart, e.g. "Florenz, Toskana, Italien"
 * @param countryCode ISO 3166-1 alpha-2 code in upper case, e.g. "IT"
 */
public record PlaceResponse(String name,
                            String displayName,
                            double latitude,
                            double longitude,
                            String countryCode) {
}
