package org.example.backend.service;

import org.example.backend.dto.FirstStopRequest;
import org.example.backend.dto.StopResponse;
import org.example.backend.dto.TripRequest;
import org.example.backend.dto.TripResponse;
import org.example.backend.model.Stop;
import org.example.backend.model.Trip;
import org.example.backend.model.TripTag;
import org.example.backend.repository.TripRepo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TripService {

    private final TripRepo tripRepo;

    public TripService(TripRepo tripRepo) {
        this.tripRepo = tripRepo;
    }

    // Trips of the user, newest first; the owner id is not sent to the client
    public List<TripResponse> getAllTrips(String userId) {
        return tripRepo.findAllByUserIdOrderByStartDateDesc(userId).stream()
                .map(TripService::toResponse)
                .toList();
    }

    public TripResponse createTrip(String userId, TripRequest request) {
        checkDateRange(request);
        FirstStopRequest firstStop = request.firstStop();
        if (firstStop == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bitte einen Ort auswählen.");
        }
        Stop stop = new Stop(UUID.randomUUID().toString(),
                firstStop.name().trim(),
                new GeoJsonPoint(firstStop.longitude(), firstStop.latitude()),
                firstStop.countryCode(),
                request.startDate(),
                null,
                Boolean.TRUE.equals(firstStop.overnight()),
                List.of());
        Trip trip = new Trip(
                null,
                customTitle(request.title()),
                request.description(),
                request.startDate(),
                request.endDate(),
                request.coverColor(),
                normalizeTags(request.tags()),
                List.of(stop),
                userId,
                null);
        return toResponse(tripRepo.save(trip));
    }

    static TripResponse toResponse(Trip trip) {
        List<Stop> stops = stopsOf(trip);
        List<GeoJsonPoint> route = stops.stream().map(Stop::location).toList();
        return new TripResponse(trip.id(), TripTitles.of(trip.title(), stops), trip.title(), trip.description(),
                trip.startDate(), trip.endDate(), trip.coverColor(), normalizeTags(trip.tags()),
                toStopResponses(stops), RouteLengthCalculator.totalKm(route), coverPhotoIdOf(trip));
    }

    // An empty title is stored as null: the trip is then named after its stops
    private static String customTitle(String title) {
        return title == null || title.isBlank() ? null : title.trim();
    }

    // Trips saved before stops existed have no stops field
    static List<Stop> stopsOf(Trip trip) {
        return trip.stops() == null ? List.of() : trip.stops();
    }

    static List<String> photoIdsOf(Stop stop) {
        return stop.photoIds() == null ? List.of() : stop.photoIds();
    }

    static List<String> allPhotoIdsOf(Trip trip) {
        return stopsOf(trip).stream()
                .flatMap(stop -> photoIdsOf(stop).stream())
                .toList();
    }

    static String coverPhotoIdOf(Trip trip) {
        List<String> photoIds = allPhotoIdsOf(trip);
        if (trip.coverPhotoId() != null && photoIds.contains(trip.coverPhotoId())) {
            return trip.coverPhotoId();
        }
        return photoIds.isEmpty() ? null : photoIds.getFirst();
    }

    private static List<StopResponse> toStopResponses(List<Stop> stops) {
        List<StopResponse> responses = new ArrayList<>();
        Stop previous = null;
        for (Stop stop : stops) {
            Long distance = previous == null
                    ? null
                    : Math.round(RouteLengthCalculator.distanceKm(previous.location(), stop.location()));
            responses.add(new StopResponse(stop.id(), stop.name(), stop.location().getY(), stop.location().getX(),
                    stop.countryCode(), stop.date(), stop.note(), Boolean.TRUE.equals(stop.overnight()), distance, photoIdsOf(stop)));
            previous = stop;
        }
        return responses;
    }

    // A stop must lie within the dates of its trip
    static boolean isOutsideTrip(LocalDate date, LocalDate tripStart, LocalDate tripEnd) {
        return date.isBefore(tripStart) || date.isAfter(tripEnd);
    }

    // No tags (also trips saved before tags existed) become an empty list;
    // duplicates are removed and the order follows the TripTag enum
    private static List<TripTag> normalizeTags(List<TripTag> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    public TripResponse getTrip(String userId, String id) {
        return toResponse(findTripOfUser(userId, id));
    }

    Trip findTripOfUser(String userId, String id) {
        return tripRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reise nicht gefunden."));
    }

    public TripResponse updateTrip(String userId, String id, TripRequest request) {
        checkDateRange(request);
        Trip trip = findTripOfUser(userId, id);
        boolean stopOutsideNewDates = stopsOf(trip).stream()
                .anyMatch(stop -> isOutsideTrip(stop.date(), request.startDate(), request.endDate()));
        if (stopOutsideNewDates) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Einige Orte liegen außerhalb des neuen Zeitraums. Ändere zuerst ihr Datum.");
        }
        Trip updated = trip
                .withTitle(customTitle(request.title()))
                .withDescription(request.description())
                .withStartDate(request.startDate())
                .withEndDate(request.endDate())
                .withCoverColor(request.coverColor())
                .withTags(normalizeTags(request.tags()));
        return toResponse(tripRepo.save(updated));
    }

    private static void checkDateRange(TripRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Das Enddatum darf nicht vor dem Startdatum liegen.");
        }
    }

    public void deleteTrip(String userId, String id) {
        tripRepo.delete(findTripOfUser(userId, id));
    }
}