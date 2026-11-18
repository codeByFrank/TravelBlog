package org.example.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouteLengthCalculatorTest {

    // GeoJSON order: longitude, latitude
    private static final GeoJsonPoint ROME = new GeoJsonPoint(12.4964, 41.9028);
    private static final GeoJsonPoint FLORENCE = new GeoJsonPoint(11.2558, 43.7696);
    private static final GeoJsonPoint VENICE = new GeoJsonPoint(12.3155, 45.4408);

    @Test
    void distanceKm_romeToFlorence_isAbout231Km() {
        assertEquals(231, RouteLengthCalculator.distanceKm(ROME, FLORENCE), 1);
    }

    @Test
    void distanceKm_isZero_forTheSamePlace() {
        assertEquals(0, RouteLengthCalculator.distanceKm(ROME, ROME), 0.001);
    }

    @Test
    void totalKm_addsTheDistancesBetweenConsecutivePlaces() {
        // Rome → Florence ≈ 231 km, Florence → Venice ≈ 204 km
        assertEquals(435, RouteLengthCalculator.totalKm(List.of(ROME, FLORENCE, VENICE)));
    }

    @Test
    void totalKm_isZero_forFewerThanTwoPlaces() {
        assertEquals(0, RouteLengthCalculator.totalKm(List.of()));
        assertEquals(0, RouteLengthCalculator.totalKm(List.of(ROME)));
    }
}
