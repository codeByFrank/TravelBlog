package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.TripRequest;
import org.example.backend.model.Trip;
import org.example.backend.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public List<Trip> getAllTrips(@AuthenticationPrincipal String userId) {
        return tripService.getAllTrips(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Trip createTrip(@AuthenticationPrincipal String userId,
                           @Valid @RequestBody TripRequest request) {
        return tripService.createTrip(userId, request);
    }
}
