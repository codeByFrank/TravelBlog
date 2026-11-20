package org.example.backend.controller;

import org.example.backend.dto.TripResponse;
import org.example.backend.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping("/api/trips/{tripId}/stops/{stopId}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse addPhoto(@AuthenticationPrincipal String userId,
                                 @PathVariable String tripId,
                                 @PathVariable String stopId,
                                 @RequestParam("file") MultipartFile file) {
        return photoService.addPhoto(userId, tripId, stopId, file);
    }
}