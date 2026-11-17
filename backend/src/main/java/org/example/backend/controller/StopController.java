package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.StopRequest;
import org.example.backend.dto.TripResponse;
import org.example.backend.service.StopService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Stops of a trip. Every endpoint returns the whole trip with its stops in the new order.
 */
@RestController
@RequestMapping("/api/trips/{tripId}/stops")
public class StopController {

    private final StopService stopService;

    public StopController(StopService stopService) {
        this.stopService = stopService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse addStop(@AuthenticationPrincipal String userId,
                                @PathVariable String tripId,
                                @Valid @RequestBody StopRequest request) {
        return stopService.addStop(userId, tripId, request);
    }

    @PutMapping("/{stopId}")
    public TripResponse updateStop(@AuthenticationPrincipal String userId,
                                   @PathVariable String tripId,
                                   @PathVariable String stopId,
                                   @Valid @RequestBody StopRequest request) {
        return stopService.updateStop(userId, tripId, stopId, request);
    }

    @DeleteMapping("/{stopId}")
    public TripResponse deleteStop(@AuthenticationPrincipal String userId,
                                   @PathVariable String tripId,
                                   @PathVariable String stopId) {
        return stopService.deleteStop(userId, tripId, stopId);
    }
}
