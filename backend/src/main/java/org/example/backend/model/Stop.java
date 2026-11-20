package org.example.backend.model;

import lombok.With;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;
import java.util.List;

/**
 * A place visited during a trip, stored inside the trip document.
 *
 * @param id          generated when the stop is added, so that it can be changed or deleted later
 * @param name        e.g. "Florenz"
 * @param location    GeoJSON point: x is the longitude, y the latitude
 * @param countryCode ISO 3166-1 alpha-2 code, e.g. "IT", used by the world map and the statistics
 * @param date        arrival date, within the dates of the trip
 * @param note        personal note; optional
 * @param overnight   true if the user stayed overnight here, false for a day visit or passing through;
 *                    null for stops saved before this field existed (treated as false)
 */
@With
public record Stop(String id,
                   String name,
                   GeoJsonPoint location,
                   String countryCode,
                   LocalDate date,
                   String note,
                   Boolean overnight,
                   List<String> photoIds) {
}
