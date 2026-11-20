package org.example.backend.service;

import org.example.backend.dto.StopResponse;
import org.example.backend.model.Stop;
import org.example.backend.model.StopType;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The route of a trip, built from its stops in the order of the visit (see architecture.md → Trips and Stops).
 * The overnight stops and the stops passed on the way are the route; a day trip goes there and back from the last
 * overnight stop before it (its base) and is not part of the route. A trip without any overnight stop, e.g. a single
 * day, has no base, so all its stops are the route.
 */
final class TripRoute {

    private final List<Stop> stops;
    private final LocalDate tripEnd;
    private final boolean hasOvernightStop;

    TripRoute(List<Stop> stops, LocalDate tripEnd) {
        this.stops = stops;
        this.tripEnd = tripEnd;
        this.hasOvernightStop = stops.stream().anyMatch(stop -> stop.typeOrDefault() == StopType.OVERNIGHT);
    }

    // Sum of the straight-line distances between consecutive stops on the route, in whole km
    long lengthKm() {
        List<GeoJsonPoint> route = stops.stream()
                .filter(this::isOnRoute)
                .map(Stop::location)
                .toList();
        return RouteLengthCalculator.totalKm(route);
    }

    List<StopResponse> toResponses() {
        List<StopResponse> responses = new ArrayList<>();
        Stop previousOnRoute = null;
        Stop base = null;
        for (int i = 0; i < stops.size(); i++) {
            Stop stop = stops.get(i);
            boolean onRoute = isOnRoute(stop);
            Stop from = onRoute ? previousOnRoute : base;
            Long distance = from == null
                    ? null
                    : Math.round(RouteLengthCalculator.distanceKm(from.location(), stop.location()));
            String baseStopId = !onRoute && base != null ? base.id() : null;

            responses.add(new StopResponse(stop.id(), stop.name(), stop.location().getY(), stop.location().getX(),
                    stop.countryCode(), stop.date(), departureDate(i), stop.note(), stop.typeOrDefault(), onRoute,
                    distance, baseStopId, TripService.photoIdsOf(stop)));

            if (onRoute) {
                previousOnRoute = stop;
            }
            if (stop.typeOrDefault() == StopType.OVERNIGHT) {
                base = stop;
            }
        }
        return responses;
    }

    private boolean isOnRoute(Stop stop) {
        return !hasOvernightStop || stop.typeOrDefault() != StopType.DAY_TRIP;
    }

    // An overnight stay lasts until the arrival at the next overnight stop, the last one until the end of the trip.
    // The only stop of a trip lasts the whole trip, whatever its type.
    private LocalDate departureDate(int index) {
        Stop stop = stops.get(index);
        if (stops.size() == 1) {
            return latest(stop.date(), tripEnd);
        }
        if (stop.typeOrDefault() != StopType.OVERNIGHT) {
            return stop.date();
        }
        for (int i = index + 1; i < stops.size(); i++) {
            Stop next = stops.get(i);
            if (next.typeOrDefault() == StopType.OVERNIGHT) {
                return next.date();
            }
        }
        return latest(stop.date(), tripEnd);
    }

    // The trip end is never before a stop, because stops lie within the trip; this only guards old data
    private static LocalDate latest(LocalDate date, LocalDate other) {
        return other.isAfter(date) ? other : date;
    }
}
