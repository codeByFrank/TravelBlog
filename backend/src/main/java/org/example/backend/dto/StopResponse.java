package org.example.backend.dto;

import org.example.backend.model.StopType;

import java.time.LocalDate;

/**
 * A stop as the frontend gets it: plain latitude and longitude instead of the GeoJSON point.
 *
 * @param date                   arrival date
 * @param departureDate          last day of the stay: for an overnight stop the arrival at the next overnight stop or
 *                               the end of the trip, for the only stop of a trip the end of the trip, otherwise
 *                               the arrival date
 * @param type                   how the user spent the time here
 * @param onRoute                true if the route line of the trip connects this stop
 * @param distanceFromPreviousKm straight-line distance in km: for a stop on the route from the previous stop on the
 *                               route, for a day trip from its base; null if there is none
 * @param baseStopId             for a day trip: the id of the overnight stop the user started from; otherwise null
 */
public record StopResponse(String id,
                           String name,
                           double latitude,
                           double longitude,
                           String countryCode,
                           LocalDate date,
                           LocalDate departureDate,
                           String note,
                           StopType type,
                           boolean onRoute,
                           Long distanceFromPreviousKm,
                           String baseStopId) {
}
