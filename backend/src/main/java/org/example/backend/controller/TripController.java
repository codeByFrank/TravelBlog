package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.CoverRequest;
import org.example.backend.dto.TripRequest;
import org.example.backend.dto.TripResponse;
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
    public List<TripResponse> getAllTrips(@AuthenticationPrincipal String userId) {
        return tripService.getAllTrips(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse createTrip(@AuthenticationPrincipal String userId,
                                   @Valid @RequestBody TripRequest request) {
        return tripService.createTrip(userId, request);
    }

    @GetMapping("/{id}")
    public TripResponse getTrip(@AuthenticationPrincipal String userId, @PathVariable String id) {
        return tripService.getTrip(userId, id);
    }

    @PutMapping("/{id}")
    public TripResponse updateTrip(@AuthenticationPrincipal String userId,
                                   @PathVariable String id,
                                   @Valid @RequestBody TripRequest request) {
        return tripService.updateTrip(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@AuthenticationPrincipal String userId, @PathVariable String id) {
        tripService.deleteTrip(userId, id);
    }

    @PutMapping("/{id}/cover")
    public TripResponse setCover(@AuthenticationPrincipal String userId,
                                 @PathVariable String id,
                                 @RequestBody CoverRequest request) {
        return tripService.setCover(userId, id, request);
    }
}