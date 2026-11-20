package org.example.backend.service;

import org.example.backend.dto.StopResponse;
import org.example.backend.model.Stop;
import org.example.backend.model.StopType;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TripRouteTest {

    // Trip from 12 to 20 May 2024
    private static final LocalDate TRIP_END = LocalDate.of(2024, 5, 20);

    private static Stop stop(String name, double longitude, double latitude, int dayOfMay, StopType type) {
        return new Stop(name, name, new GeoJsonPoint(longitude, latitude), "IT", LocalDate.of(2024, 5, dayOfMay),
                null, type, null, List.of());
    }

    private static Stop rome(StopType type) {
        return stop("Rom", 12.4964, 41.9028, 12, type);
    }

    private static Stop siena(StopType type) {
        return stop("Siena", 11.3308, 43.3188, 15, type);
    }

    private static Stop florence(StopType type) {
        return stop("Florenz", 11.2558, 43.7696, 15, type);
    }

    private static Stop pisa(StopType type) {
        return stop("Pisa", 10.4017, 43.7228, 17, type);
    }

    @Test
    void passingThroughIsPartOfTheRoute() {
        TripRoute route = new TripRoute(List.of(rome(StopType.OVERNIGHT), siena(StopType.PASSING_THROUGH),
                florence(StopType.OVERNIGHT)), TRIP_END);
        List<StopResponse> stops = route.toResponses();

        assertTrue(stops.get(1).onRoute());
        assertNull(stops.get(1).baseStopId());
        long sumOfDistances = stops.get(1).distanceFromPreviousKm() + stops.get(2).distanceFromPreviousKm();
        assertTrue(Math.abs(sumOfDistances - route.lengthKm()) <= 1);
    }

    @Test
    void dayTripIsNotPartOfTheRoute_andIsMeasuredFromItsBase() {
        TripRoute withDayTrip = new TripRoute(List.of(rome(StopType.OVERNIGHT), florence(StopType.OVERNIGHT),
                pisa(StopType.DAY_TRIP)), TRIP_END);
        TripRoute withoutDayTrip = new TripRoute(List.of(rome(StopType.OVERNIGHT), florence(StopType.OVERNIGHT)),
                TRIP_END);
        StopResponse pisa = withDayTrip.toResponses().get(2);

        assertFalse(pisa.onRoute());
        assertEquals("Florenz", pisa.baseStopId());
        assertEquals(69, pisa.distanceFromPreviousKm());
        assertEquals(withoutDayTrip.lengthKm(), withDayTrip.lengthKm());
    }

    @Test
    void allStopsAreTheRoute_whenNoStopIsOvernight() {
        TripRoute route = new TripRoute(List.of(florence(StopType.DAY_TRIP), pisa(StopType.DAY_TRIP)), TRIP_END);
        List<StopResponse> stops = route.toResponses();

        assertTrue(stops.get(1).onRoute());
        assertEquals(69, stops.get(1).distanceFromPreviousKm());
        assertEquals(69, route.lengthKm());
    }

    @Test
    void dayTripBeforeTheFirstOvernightStop_hasNoBase() {
        StopResponse first = new TripRoute(List.of(rome(StopType.DAY_TRIP), florence(StopType.OVERNIGHT)), TRIP_END)
                .toResponses().getFirst();

        assertFalse(first.onRoute());
        assertNull(first.baseStopId());
        assertNull(first.distanceFromPreviousKm());
    }

    @Test
    void overnightStayLastsUntilTheNextOvernightStop_orTheEndOfTheTrip() {
        List<StopResponse> stops = new TripRoute(List.of(rome(StopType.OVERNIGHT), siena(StopType.PASSING_THROUGH),
                florence(StopType.OVERNIGHT), pisa(StopType.DAY_TRIP)), TRIP_END).toResponses();

        assertEquals(LocalDate.of(2024, 5, 15), stops.get(0).departureDate());
        assertEquals(LocalDate.of(2024, 5, 15), stops.get(1).departureDate());
        assertEquals(TRIP_END, stops.get(2).departureDate());
        assertEquals(LocalDate.of(2024, 5, 17), stops.get(3).departureDate());
    }

    @Test
    void theOnlyStopLastsTheWholeTrip_whateverItsType() {
        StopResponse only = new TripRoute(List.of(rome(StopType.DAY_TRIP)), TRIP_END).toResponses().getFirst();

        assertEquals(TRIP_END, only.departureDate());
        assertEquals(0, new TripRoute(List.of(rome(StopType.OVERNIGHT)), TRIP_END).lengthKm());
    }
}
