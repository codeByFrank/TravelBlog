package org.example.backend.service;

import org.example.backend.model.Stop;
import org.example.backend.model.Trip;
import org.example.backend.model.User;
import org.example.backend.model.WishlistEntry;
import org.example.backend.repository.TripRepo;
import org.example.backend.repository.UserRepo;
import org.example.backend.repository.WishlistRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates the demo user with sample trips, photos and wishlist entries, so that anyone can try TravelBlog
 * without creating an account (e.g. in a presentation).
 * <p>
 * Runs once when the application starts, but only when the environment variable {@code DEMO_PASSWORD} is set
 * (on Render). Locally and in the tests it does nothing.
 * <p>
 * Every start replaces the data of the demo user with the sample data. Visitors may change or delete the demo
 * trips; after the next restart everything looks as before. The data of other users is never touched.
 */
@Component
public class DemoDataSeeder implements ApplicationRunner {

    static final String DEMO_USERNAME = "demo";

    private static final Logger LOG = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final UserRepo userRepo;
    private final TripRepo tripRepo;
    private final WishlistRepo wishlistRepo;
    private final PhotoStorage photoStorage;
    private final PasswordEncoder passwordEncoder;
    private final String demoPassword;

    public DemoDataSeeder(UserRepo userRepo,
                          TripRepo tripRepo,
                          WishlistRepo wishlistRepo,
                          PhotoStorage photoStorage,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.demo.password:}") String demoPassword) {
        this.userRepo = userRepo;
        this.tripRepo = tripRepo;
        this.wishlistRepo = wishlistRepo;
        this.photoStorage = photoStorage;
        this.passwordEncoder = passwordEncoder;
        this.demoPassword = demoPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (demoPassword == null || demoPassword.isBlank()) {
            // No DEMO_PASSWORD: this installation has no demo user
            return;
        }
        seedDemoData();
    }

    // Creates the demo user if needed and gives it fresh sample data
    void seedDemoData() {
        User demoUser = saveDemoUser();
        deleteDataOf(demoUser.id());

        // The trips are saved first: the photos need the id of their trip
        List<Trip> savedTrips = tripRepo.saveAll(DemoData.trips(demoUser.id()));
        for (Trip trip : savedTrips) {
            tripRepo.save(withPhotos(trip));
        }
        wishlistRepo.saveAll(DemoData.wishlist(demoUser.id()));
        LOG.info("Demo data created for the user '{}'", DEMO_USERNAME);
    }

    // Stores the sample photo of each stop (see DemoData.PHOTO_OF_STOP) and sets the cover photo of the trip
    private Trip withPhotos(Trip trip) {
        List<Stop> stopsWithPhotos = new ArrayList<>();
        String coverPhotoId = null;

        for (Stop stop : trip.stops()) {
            String fileName = DemoData.PHOTO_OF_STOP.get(stop.name());
            String photoId = fileName == null ? null : savePhoto(fileName, trip);
            if (photoId == null) {
                stopsWithPhotos.add(stop);
            } else {
                stopsWithPhotos.add(stop.withPhotoIds(List.of(photoId)));
                if (DemoData.COVER_STOPS.contains(stop.name())) {
                    coverPhotoId = photoId;
                }
            }
        }
        return trip.withStops(stopsWithPhotos).withCoverPhotoId(coverPhotoId);
    }

    // Returns the id of the stored photo, or null if the file is missing: the demo then simply has fewer photos
    private String savePhoto(String fileName, Trip trip) {
        ClassPathResource photo = new ClassPathResource(DemoData.PHOTO_FOLDER + fileName);
        try (InputStream content = photo.getInputStream()) {
            return photoStorage.save(content, fileName, "image/jpeg", trip.userId(), trip.id());
        } catch (IOException e) {
            LOG.warn("Demo photo '{}' could not be stored", fileName, e);
            return null;
        }
    }

    // The password always comes from DEMO_PASSWORD, so changing the variable on Render changes the password
    private User saveDemoUser() {
        String passwordHash = passwordEncoder.encode(demoPassword);
        User demoUser = userRepo.findByUsername(DEMO_USERNAME)
                .map(existingUser -> existingUser.withPasswordHash(passwordHash))
                .orElse(new User(null, DEMO_USERNAME, passwordHash));
        return userRepo.save(demoUser);
    }

    // Removes the trips (with their photos) and the wishlist entries of the demo user
    private void deleteDataOf(String userId) {
        List<Trip> trips = tripRepo.findAllByUserIdOrderByStartDateDesc(userId);
        for (Trip trip : trips) {
            photoStorage.deleteAllOfTrip(trip.id());
        }
        tripRepo.deleteAll(trips);

        List<WishlistEntry> wishlistEntries = wishlistRepo.findAllByUserId(userId);
        wishlistRepo.deleteAll(wishlistEntries);
    }
}
