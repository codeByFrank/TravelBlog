package org.example.backend.service;

import org.example.backend.dto.StopRequest;
import org.example.backend.dto.TripResponse;
import org.example.backend.model.Stop;
import org.example.backend.model.Trip;
import org.example.backend.repository.TripRepo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Adds, changes and deletes the stops of a trip.
 * Every change returns the whole trip, because the order of the stops and the distances may change.
 */
@Service
public class StopService {

    private final TripRepo tripRepo;
    private final TripService tripService;

    public StopService(TripRepo tripRepo, TripService tripService) {
        this.tripRepo = tripRepo;
        this.tripService = tripService;
    }

    public TripResponse addStop(String userId, String tripId, StopRequest request) {
        Trip trip = tripService.findTripOfUser(userId, tripId);
        checkDateWithinTrip(trip, request);
        List<Stop> stops = new ArrayList<>(TripService.stopsOf(trip));
        stops.add(toStop(UUID.randomUUID().toString(), request));
        return saveWithStops(trip, stops);
    }

    public TripResponse updateStop(String userId, String tripId, String stopId, StopRequest request) {
        Trip trip = tripService.findTripOfUser(userId, tripId);
        List<Stop> stops = TripService.stopsOf(trip);
        checkStopExists(stops, stopId);
        checkDateWithinTrip(trip, request);
        List<Stop> updated = stops.stream()
                .map(stop -> stop.id().equals(stopId) ? toStop(stopId, request) : stop)
                .toList();
        return saveWithStops(trip, updated);
    }

    public TripResponse deleteStop(String userId, String tripId, String stopId) {
        Trip trip = tripService.findTripOfUser(userId, tripId);
        List<Stop> stops = TripService.stopsOf(trip);
        checkStopExists(stops, stopId);
        if (stops.size() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Eine Reise braucht mindestens einen Ort. Ändere ihn stattdessen.");
        }
        List<Stop> remaining = stops.stream()
                .filter(stop -> !stop.id().equals(stopId))
                .toList();
        return saveWithStops(trip, remaining);
    }

    // The order of the stops is the order of the visit: by date, stops with the same date in the order they were added
    private TripResponse saveWithStops(Trip trip, List<Stop> stops) {
        List<Stop> byDate = stops.stream()
                .sorted(Comparator.comparing(Stop::date))
                .toList();
        return TripService.toResponse(tripRepo.save(trip.withStops(byDate)));
    }

    private static Stop toStop(String id, StopRequest request) {
        String note = request.note() == null || request.note().isBlank() ? null : request.note().trim();
        return new Stop(id,
                request.name().trim(),
                new GeoJsonPoint(request.longitude(), request.latitude()),
                request.countryCode(),
                request.date(),
                note,
                Boolean.TRUE.equals(request.overnight()));
    }

    private static void checkDateWithinTrip(Trip trip, StopRequest request) {
        if (TripService.isOutsideTrip(request.date(), trip.startDate(), trip.endDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Das Datum muss innerhalb der Reise liegen.");
        }
    }

    private static void checkStopExists(List<Stop> stops, String stopId) {
        boolean exists = stops.stream().anyMatch(stop -> stop.id().equals(stopId));
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ort nicht gefunden.");
        }
    }
}
