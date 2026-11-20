package org.example.backend.service;

import org.example.backend.dto.TripResponse;
import org.example.backend.model.Stop;
import org.example.backend.model.Trip;
import org.example.backend.repository.TripRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pro Ort sind höchstens 5 Fotos möglich.");
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bitte ein Foto im Format JPEG, PNG oder WebP wählen.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ein Foto darf höchstens 5 MB groß sein.");
        }
    }

    private static Stop findStop(Trip trip, String stopId) {
        return TripService.stopsOf(trip).stream()
                .filter(stop -> stop.id().equals(stopId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ort nicht gefunden."));
    }

    private static Trip withStop(Trip trip, Stop changed) {
        List<Stop> stops = TripService.stopsOf(trip).stream()
                .map(stop -> stop.id().equals(changed.id()) ? changed : stop)
                .toList();
        return trip.withStops(stops);
    }
}