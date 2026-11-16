package org.example.backend.service;

import org.example.backend.dto.TripResponse;
import org.example.backend.model.Trip;
import org.example.backend.repository.TripRepo;
import org.springframework.stereotype.Service;

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

    private static TripResponse toResponse(Trip trip) {
        return new TripResponse(trip.id(), trip.title(), trip.description(), trip.startDate(), trip.endDate());
    }
}
