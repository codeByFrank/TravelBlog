package org.example.backend.controller;

import org.example.backend.dto.TripResponse;
import org.example.backend.service.PhotoService;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

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

    @GetMapping("/api/photos/{photoId}")
    public ResponseEntity<Resource> getPhoto(@AuthenticationPrincipal String userId,
                                             @PathVariable String photoId) {
        GridFsResource photo = photoService.getPhoto(userId, photoId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                // A photo never changes: the browser may keep it, but only for this user
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePrivate())
                .body(photo);
    }

    @DeleteMapping("/api/trips/{tripId}/stops/{stopId}/photos/{photoId}")
    public TripResponse deletePhoto(@AuthenticationPrincipal String userId,
                                    @PathVariable String tripId,
                                    @PathVariable String stopId,
                                    @PathVariable String photoId) {
        return photoService.deletePhoto(userId, tripId, stopId, photoId);
    }
}