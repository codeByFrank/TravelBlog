package org.example.backend.controller;

import org.example.backend.dto.TripResponse;
import org.example.backend.service.TripService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
