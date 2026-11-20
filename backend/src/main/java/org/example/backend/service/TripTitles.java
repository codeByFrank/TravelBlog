package org.example.backend.service;

import org.example.backend.model.Stop;
import org.example.backend.model.StopType;

import java.util.List;
import java.util.stream.Stream;

/**
 * Builds the title of a trip that has no title of its own from the names of its stops,
 * e.g. "Rom – Florenz – Venedig". The title is built on every read, so it follows the stops.
 */
final class TripTitles {

    static final String FALLBACK = "Neue Reise";
    private static final int MAX_NAMES = 3;
    private static final String SEPARATOR = " – ";

    private TripTitles() {
    }

    static String of(String customTitle, List<Stop> stops) {
        if (customTitle != null && !customTitle.isBlank()) {
            return customTitle;
        }
        // The places where the user stayed overnight describe the trip best; a day trip has none
        List<String> overnight = distinctNames(stops.stream()
                .filter(stop -> stop.typeOrDefault() == StopType.OVERNIGHT));
        List<String> names = overnight.isEmpty() ? distinctNames(stops.stream()) : overnight;
        if (names.isEmpty()) {
            // Only trips saved before every trip needed a place
            return FALLBACK;
        }
        if (names.size() <= MAX_NAMES) {
            return String.join(SEPARATOR, names);
        }
        return String.join(SEPARATOR, names.subList(0, MAX_NAMES)) + " und " + (names.size() - MAX_NAMES) + " weitere";
    }

    // A round trip back to the first place names it only once
    private static List<String> distinctNames(Stream<Stop> stops) {
        return stops.map(Stop::name).distinct().toList();
    }
}
