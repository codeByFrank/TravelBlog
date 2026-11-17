package org.example.backend.model;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;

/**
 * A place visited during a trip, stored inside the trip document.
 *
 * @param id          generated when the stop is added, so that it can be changed or deleted later
 * @param name        e.g. "Florenz"
 * @param location    GeoJSON point: x is the longitude, y the latitude
 * @param countryCode ISO 3166-1 alpha-2 code, e.g. "IT", used by the world map and the statistics
 * @param date        arrival date, within the dates of the trip
 * @param note        personal note; optional
 */
public record Stop(String id,
                   String name,
                   GeoJsonPoint location,
                   String countryCode,
                   LocalDate date,
                   String note) {
}
