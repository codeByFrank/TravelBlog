package org.example.backend.service;

import org.example.backend.dto.TripRequest;
import org.example.backend.dto.TripResponse;
import org.example.backend.model.Trip;
import org.example.backend.repository.TripRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
        Trip trip = new Trip(
                null,
                request.title().trim(),
                request.description(),
                request.startDate(),
                request.endDate(),
                request.coverColor(),
                userId);
        return toResponse(tripRepo.save(trip));
    }

    private static TripResponse toResponse(Trip trip) {
        return new TripResponse(trip.id(), trip.title(), trip.description(),
                trip.startDate(), trip.endDate(), trip.coverColor());
    }

    public TripResponse getTrip(String userId, String id) {
        return toResponse(findTripOfUser(userId, id));
    }

    private Trip findTripOfUser(String userId, String id) {
        return tripRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reise nicht gefunden."));
    }

    public TripResponse updateTrip(String userId, String id, TripRequest request) {
        checkDateRange(request);
        Trip updated = findTripOfUser(userId, id)
                .withTitle(request.title().trim())
                .withDescription(request.description())
                .withStartDate(request.startDate())
                .withEndDate(request.endDate())
                .withCoverColor(request.coverColor());
        return toResponse(tripRepo.save(updated));
    }

    private static void checkDateRange(TripRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Das Enddatum darf nicht vor dem Startdatum liegen.");
        }
    }
}