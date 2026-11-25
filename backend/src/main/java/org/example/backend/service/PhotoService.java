package org.example.backend.service;

import org.example.backend.dto.TripResponse;
import org.example.backend.exception.LocalizedException;
import org.example.backend.model.Stop;
import org.example.backend.model.Trip;
import org.example.backend.repository.TripRepo;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PhotoService {

    static final int MAX_PHOTOS_PER_STOP = 5;
    static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final TripRepo tripRepo;
    private final TripService tripService;
    private final PhotoStorage photoStorage;

    public PhotoService(TripRepo tripRepo, TripService tripService, PhotoStorage photoStorage) {
        this.tripRepo = tripRepo;
        this.tripService = tripService;
        this.photoStorage = photoStorage;
    }

    public TripResponse addPhoto(String userId, String tripId, String stopId, MultipartFile file) {
        Trip trip = tripService.findTripOfUser(userId, tripId);
        Stop stop = findStop(trip, stopId);
        List<String> photoIds = TripService.photoIdsOf(stop);
        if (photoIds.size() >= MAX_PHOTOS_PER_STOP) {
            throw new LocalizedException(HttpStatus.BAD_REQUEST, "error.photo.tooMany");
        }
        checkFile(file);

        String photoId = photoStorage.save(file, userId, tripId);
        List<String> updatedIds = new ArrayList<>(photoIds);
        updatedIds.add(photoId);
        Trip updated = withStop(trip, stop.withPhotoIds(updatedIds));
        return TripService.toResponse(tripRepo.save(updated));
    }

    private static void checkFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (file.isEmpty() || contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new LocalizedException(HttpStatus.BAD_REQUEST,
                    "error.photo.wrongType");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new LocalizedException(HttpStatus.BAD_REQUEST, "error.photo.tooLarge");
        }
    }

    private static Stop findStop(Trip trip, String stopId) {
        return TripService.stopsOf(trip).stream()
                .filter(stop -> stop.id().equals(stopId))
                .findFirst()
                .orElseThrow(() -> new LocalizedException(HttpStatus.NOT_FOUND, "error.stop.notFound"));
    }

    private static Trip withStop(Trip trip, Stop changed) {
        List<Stop> stops = TripService.stopsOf(trip).stream()
                .map(stop -> stop.id().equals(changed.id()) ? changed : stop)
                .toList();
        return trip.withStops(stops);
    }

    public GridFsResource getPhoto(String userId, String photoId) {
        return photoStorage.find(photoId, userId)
                .orElseThrow(() -> new LocalizedException(HttpStatus.NOT_FOUND, "error.photo.notFound"));
    }

    public TripResponse deletePhoto(String userId, String tripId, String stopId, String photoId) {
        Trip trip = tripService.findTripOfUser(userId, tripId);
        Stop stop = findStop(trip, stopId);
        List<String> photoIds = TripService.photoIdsOf(stop);
        if (!photoIds.contains(photoId)) {
            throw new LocalizedException(HttpStatus.NOT_FOUND, "error.photo.notFound");
        }

        List<String> remaining = photoIds.stream()
                .filter(id -> !id.equals(photoId))
                .toList();
        Trip updated = withStop(trip, stop.withPhotoIds(remaining));
        if (photoId.equals(trip.coverPhotoId())) {
            updated = updated.withCoverPhotoId(null);
        }
        Trip saved = tripRepo.save(updated);
        photoStorage.delete(List.of(photoId));
        return TripService.toResponse(saved);
    }
}