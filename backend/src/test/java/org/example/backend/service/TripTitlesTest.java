package org.example.backend.service;

import org.example.backend.model.Stop;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TripTitlesTest {

    private static Stop stop(String name, Boolean overnight) {
        return new Stop(name, name, new GeoJsonPoint(0, 0), "IT", LocalDate.of(2024, 5, 12), null, overnight, List.of());
    }

    @Test
    void of_returnsOwnTitle_whenUserHasOne() {
        assertEquals("Italien im Mai", TripTitles.of("Italien im Mai", List.of(stop("Rom", true))));
    }

    @Test
    void of_usesOnlyOvernightStops() {
        List<Stop> stops = List.of(stop("Rom", true), stop("Pisa", false), stop("Florenz", true), stop("Siena", null));

        assertEquals("Rom – Florenz", TripTitles.of(null, stops));
    }

    @Test
    void of_usesAllStops_whenNoStopIsOvernight() {
        assertEquals("Potsdam", TripTitles.of(" ", List.of(stop("Potsdam", false))));
    }

    @Test
    void of_namesEachPlaceOnce() {
        List<Stop> stops = List.of(stop("Rom", true), stop("Florenz", true), stop("Rom", true));

        assertEquals("Rom – Florenz", TripTitles.of(null, stops));
    }

    @Test
    void of_shortensLongTitles() {
        List<Stop> stops = List.of(stop("Rom", true), stop("Florenz", true), stop("Bologna", true),
                stop("Venedig", true), stop("Mailand", true));

        assertEquals("Rom – Florenz – Bologna und 2 weitere", TripTitles.of(null, stops));
    }

    @Test
    void of_returnsFallback_whenTripHasNoStops() {
        assertEquals("Neue Reise", TripTitles.of(null, List.of()));
    }
}
