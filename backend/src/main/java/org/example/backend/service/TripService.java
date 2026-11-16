package org.example.backend.service;

import org.example.backend.dto.TripRequest;
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
    
    public List<Trip> getAllTrips(String userId) {
        return tripRepo.findAllByUserId(userId);
    }

    public Trip createTrip(String userId, TripRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Das Enddatum darf nicht vor dem Startdatum liegen.");
        }
        Trip trip = new Trip(
                null,
                request.title().trim(),
                request.description(),
                request.startDate(),
                request.endDate(),
                request.coverColor(),
                userId);
        return tripRepo.save(trip);
    }
}
