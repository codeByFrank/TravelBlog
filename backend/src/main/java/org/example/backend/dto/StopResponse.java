package org.example.backend.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * A stop as the frontend gets it: plain latitude and longitude instead of the GeoJSON point.
 *
 * @param overnight              true if the user stayed overnight here
 * @param distanceFromPreviousKm straight-line distance from the previous stop in km; null for the first stop
 */
public record StopResponse(String id,
                           String name,
                           double latitude,
                           double longitude,
                           String countryCode,
                           LocalDate date,
                           String note,
                           boolean overnight,
                           Long distanceFromPreviousKm,
                           List<String> photoIds) {
}
