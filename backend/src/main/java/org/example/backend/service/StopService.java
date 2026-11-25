package org.example.backend.service;

import org.example.backend.dto.StopRequest;
import org.example.backend.dto.TripResponse;
import org.example.backend.exception.LocalizedException;
import org.example.backend.model.Stop;
import org.example.backend.model.Trip;
import org.example.backend.repository.TripRepo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    private final PhotoStorage photoStorage;

    public StopService(TripRepo tripRepo, TripService tripService, PhotoStorage photoStorage) {
        this.tripRepo = tripRepo;
        this.tripService = tripService;
        this.photoStorage = photoStorage;
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
        // The request has no photos, so the stop keeps the ones it has
        List<Stop> updated = stops.stream()
                .map(stop -> stop.id().equals(stopId) ? toStop(stopId, request).withPhotoIds(TripService.photoIdsOf(stop)) : stop)
                .toList();
        return saveWithStops(trip, updated);
    }

    public TripResponse deleteStop(String userId, String tripId, String stopId) {
        Trip trip = tripService.findTripOfUser(userId, tripId);
        List<Stop> stops = TripService.stopsOf(trip);
        Stop deleted = stops.stream()
                .filter(stop -> stop.id().equals(stopId))
                .findFirst()
                .orElseThrow(() -> new LocalizedException(HttpStatus.NOT_FOUND, "error.stop.notFound"));
        if (stops.size() == 1) {
            throw new LocalizedException(HttpStatus.BAD_REQUEST,
                    "error.stop.lastStop");
        }
        List<Stop> remaining = stops.stream()
                .filter(stop -> !stop.id().equals(stopId))
                .toList();
        List<String> deletedPhotoIds = TripService.photoIdsOf(deleted);
        Trip changed = deletedPhotoIds.contains(trip.coverPhotoId()) ? trip.withCoverPhotoId(null) : trip;

        // Save first: if saving fails, the photos still exist
        TripResponse response = saveWithStops(changed, remaining);
        photoStorage.delete(deletedPhotoIds);
        return response;
    }

    // The order of the stops is the order of the visit: by date, stops with the same date in the order they were added
    private TripResponse saveWithStops(Trip trip, List<Stop> stops) {
        List<Stop> byDate = stops.stream()
                .sorted(Comparator.comparing(Stop::date))
                .toList();
        return TripService.toResponse(tripRepo.save(trip.withStops(byDate)));
    }

    // A new stop has no photos yet; the old overnight field stays empty, the type replaces it
    private static Stop toStop(String id, StopRequest request) {
        String note = request.note() == null || request.note().isBlank() ? null : request.note().trim();
        return new Stop(id,
                request.name().trim(),
                new GeoJsonPoint(request.longitude(), request.latitude()),
                request.countryCode(),
                request.date(),
                note,
                request.type(),
                null,
                List.of());
    }

    private static void checkDateWithinTrip(Trip trip, StopRequest request) {
        if (TripService.isOutsideTrip(request.date(), trip.startDate(), trip.endDate())) {
            throw new LocalizedException(HttpStatus.BAD_REQUEST,
                    "error.stop.dateOutsideTrip");
        }
    }

    private static void checkStopExists(List<Stop> stops, String stopId) {
        boolean exists = stops.stream().anyMatch(stop -> stop.id().equals(stopId));
        if (!exists) {
            throw new LocalizedException(HttpStatus.NOT_FOUND, "error.stop.notFound");
        }
    }
}