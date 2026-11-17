package org.example.backend.model;

/**
 * Fixed list of tags a trip can be marked with.
 * The German labels ("Berge", "Meer", ...) are defined in the frontend.
 * Later, stops of a trip use the same tags.
 */
public enum TripTag {
    MOUNTAINS,
    SEA,
    CITY_TRIP,
    FOOD
}
