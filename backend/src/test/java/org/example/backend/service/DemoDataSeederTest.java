package org.example.backend.service;

import org.example.backend.model.Priority;
import org.example.backend.model.Stop;
import org.example.backend.model.Trip;
import org.example.backend.model.User;
import org.example.backend.model.WishlistEntry;
import org.example.backend.repository.TripRepo;
import org.example.backend.repository.UserRepo;
import org.example.backend.repository.WishlistRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DemoDataSeederTest {

    // Only for these tests; the real demo password is set on Render
    private static final String TEST_DEMO_PASSWORD = "demo-password-for-tests";

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TripRepo tripRepo;
    @Autowired
    private WishlistRepo wishlistRepo;
    @Autowired
    private PhotoStorage photoStorage;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        tripRepo.deleteAll();
        wishlistRepo.deleteAll();
        gridFsTemplate.delete(new Query()); // all stored photos
    }

    private DemoDataSeeder seederWithPassword(String password) {
        return new DemoDataSeeder(userRepo, tripRepo, wishlistRepo, photoStorage, passwordEncoder, password);
    }

    // Number of photos stored in GridFS for one user
    private int countPhotosOf(String userId) {
        Query query = new Query(Criteria.where("metadata.userId").is(userId));
        return gridFsTemplate.find(query).into(new ArrayList<>()).size();
    }

    @Test
    void seedDemoData_storesThePhotosAndACoverPhotoForEveryTrip() {
        seederWithPassword(TEST_DEMO_PASSWORD).seedDemoData();

        String demoUserId = userRepo.findByUsername(DemoDataSeeder.DEMO_USERNAME).orElseThrow().id();
        List<Trip> trips = tripRepo.findAllByUserIdOrderByStartDateDesc(demoUserId);
        assertEquals(DemoData.PHOTO_OF_STOP.size(), countPhotosOf(demoUserId));
        for (Trip trip : trips) {
            assertNotNull(trip.coverPhotoId(), trip.title() + " has no cover photo");
            // The cover photo can be loaded by the demo user like an uploaded photo
            assertTrue(photoStorage.find(trip.coverPhotoId(), demoUserId).isPresent());
        }
    }

    @Test
    void seedDemoData_doesNotStoreThePhotosTwice_whenRunAgain() {
        DemoDataSeeder seeder = seederWithPassword(TEST_DEMO_PASSWORD);
        seeder.seedDemoData();
        seeder.seedDemoData();

        String demoUserId = userRepo.findByUsername(DemoDataSeeder.DEMO_USERNAME).orElseThrow().id();
        assertEquals(DemoData.PHOTO_OF_STOP.size(), countPhotosOf(demoUserId));
    }

    @Test
    void photos_existForStopsOfTheDemoTrips() {
        // Catches a typo in a stop name or file name in DemoData
        List<String> stopNames = DemoData.trips("demo-id").stream()
                .flatMap(trip -> trip.stops().stream())
                .map(Stop::name)
                .toList();
        DemoData.PHOTO_OF_STOP.forEach((stopName, fileName) -> {
            assertTrue(stopNames.contains(stopName), "No demo stop called " + stopName);
            assertTrue(new ClassPathResource(DemoData.PHOTO_FOLDER + fileName).exists(), "Missing photo " + fileName);
        });
        assertTrue(stopNames.containsAll(DemoData.COVER_STOPS));
    }

    @Test
    void run_doesNothing_withoutPassword() {
        seederWithPassword("").run(new DefaultApplicationArguments());

        assertFalse(userRepo.existsByUsername(DemoDataSeeder.DEMO_USERNAME));
        assertEquals(0, tripRepo.count());
        assertEquals(0, wishlistRepo.count());
    }

    @Test
    void run_createsDemoUserWithSampleData_withPassword() {
        seederWithPassword(TEST_DEMO_PASSWORD).run(new DefaultApplicationArguments());

        User demoUser = userRepo.findByUsername(DemoDataSeeder.DEMO_USERNAME).orElseThrow();
        assertTrue(passwordEncoder.matches(TEST_DEMO_PASSWORD, demoUser.passwordHash()));
        assertEquals(4, tripRepo.findAllByUserIdOrderByStartDateDesc(demoUser.id()).size());
        assertEquals(5, wishlistRepo.findAllByUserId(demoUser.id()).size());
    }

    @Test
    void seedDemoData_replacesTheDemoData_whenRunAgain() {
        DemoDataSeeder seeder = seederWithPassword(TEST_DEMO_PASSWORD);
        seeder.seedDemoData();
        String demoUserId = userRepo.findByUsername(DemoDataSeeder.DEMO_USERNAME).orElseThrow().id();
        // A visitor deletes a demo trip
        Trip deletedTrip = tripRepo.findAllByUserIdOrderByStartDateDesc(demoUserId).getFirst();
        tripRepo.delete(deletedTrip);

        seeder.seedDemoData();

        // Still one demo user with all sample data, nothing twice
        assertEquals(1, userRepo.count());
        assertEquals(demoUserId, userRepo.findByUsername(DemoDataSeeder.DEMO_USERNAME).orElseThrow().id());
        assertEquals(4, tripRepo.findAllByUserIdOrderByStartDateDesc(demoUserId).size());
        assertEquals(5, wishlistRepo.findAllByUserId(demoUserId).size());
    }

    @Test
    void seedDemoData_usesTheNewPassword_whenThePasswordChanged() {
        seederWithPassword(TEST_DEMO_PASSWORD).seedDemoData();

        seederWithPassword("another-demo-password").seedDemoData();

        User demoUser = userRepo.findByUsername(DemoDataSeeder.DEMO_USERNAME).orElseThrow();
        assertTrue(passwordEncoder.matches("another-demo-password", demoUser.passwordHash()));
        assertFalse(passwordEncoder.matches(TEST_DEMO_PASSWORD, demoUser.passwordHash()));
    }

    @Test
    void seedDemoData_keepsTheDataOfOtherUsers() {
        Trip otherTrip = tripRepo.save(new Trip(null, "Meine Reise", null,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5),
                null, List.of(), List.of(), "other-user", null));
        WishlistEntry otherEntry = wishlistRepo.save(new WishlistEntry(null, "other-user",
                "Island", "Nordlichter", Priority.HIGH, null, List.of("IS")));

        seederWithPassword(TEST_DEMO_PASSWORD).seedDemoData();

        assertTrue(tripRepo.existsById(otherTrip.id()));
        assertTrue(wishlistRepo.existsById(otherEntry.id()));
    }

    @Test
    void trips_haveStopsWithinTheDatesOfTheTrip() {
        // The app expects every stop date between the start and the end of its trip
        for (Trip trip : DemoData.trips("demo-id")) {
            for (Stop stop : trip.stops()) {
                assertFalse(stop.date().isBefore(trip.startDate()), trip.title() + ": " + stop.name());
                assertFalse(stop.date().isAfter(trip.endDate()), trip.title() + ": " + stop.name());
            }
        }
    }
}
