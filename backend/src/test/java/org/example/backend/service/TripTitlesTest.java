package org.example.backend.service;

import org.example.backend.model.Stop;
import org.example.backend.model.StopType;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TripTitlesTest {

    private static Stop stop(String name, StopType type) {
        return new Stop(name, name, new GeoJsonPoint(0, 0), "IT", LocalDate.of(2024, 5, 12), null, type, null);
    }

    private static Stop stop(String name) {
        return stop(name, StopType.OVERNIGHT);
    }

    @Test
    void of_returnsOwnTitle_whenUserHasOne() {
        assertEquals("Italien im Mai", TripTitles.of("Italien im Mai", List.of(stop("Rom"))));
    }

    @Test
    void of_usesOnlyOvernightStops() {
        List<Stop> stops = List.of(stop("Rom"), stop("Pisa", StopType.DAY_TRIP), stop("Florenz"), stop("Siena", StopType.PASSING_THROUGH));

        assertEquals("Rom – Florenz", TripTitles.of(null, stops));
    }

    @Test
    void of_usesAllStops_whenNoStopIsOvernight() {
        assertEquals("Potsdam", TripTitles.of(" ", List.of(stop("Potsdam", StopType.DAY_TRIP))));
    }

    @Test
    void of_namesEachPlaceOnce() {
        List<Stop> stops = List.of(stop("Rom"), stop("Florenz"), stop("Rom"));

        assertEquals("Rom – Florenz", TripTitles.of(null, stops));
    }

    @Test
    void of_shortensLongTitles() {
        List<Stop> stops = List.of(stop("Rom"), stop("Florenz"), stop("Bologna"),
                stop("Venedig"), stop("Mailand"));

        assertEquals("Rom – Florenz – Bologna und 2 weitere", TripTitles.of(null, stops));
    }

    @Test
    void of_returnsFallback_whenTripHasNoStops() {
        assertEquals("Neue Reise", TripTitles.of(null, List.of()));
    }
}
