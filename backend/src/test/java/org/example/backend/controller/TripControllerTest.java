package org.example.backend.controller;

import org.example.backend.model.Stop;
import org.example.backend.model.Trip;
import org.example.backend.model.TripTag;
import org.example.backend.repository.TripRepo;
import org.example.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.example.backend.model.StopType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class TripControllerTest {

    private static final String USER_ID = "user-1";
    private static final String OTHER_USER_ID = "user-2";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TripRepo tripRepo;
    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        tripRepo.deleteAll();
    }

    // Token of the logged-in user in all tests
    private String bearer() {
        return "Bearer " + jwtService.createToken(USER_ID);
    }

    // Saves the trip with id "1" for the given owner
    private void saveTrip(String userId) {
        tripRepo.save(new Trip("1", "Italien", "Rom und Florenz",
                LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "#DCEAF7", List.of(), List.of(), userId, null));
    }

    @Test
    void getAllTrips_returnsSavedTrip() throws Exception {
        Trip trip = new Trip("1", "TestTrip", "Test", LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "#E3F0FC",List.of(), List.of(), USER_ID, null);
        tripRepo.save(trip);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        [
                          {
                            "id": "1",
                            "title": "TestTrip",
                            "description": "Test",
                            "startDate": "2024-05-12",
                            "endDate": "2024-05-20"
                          }
                        ]
                        """))
                // The owner id stays on the server
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").doesNotExist());
    }

    @Test
    void getAllTrips_returnsNewestTripFirst() throws Exception {
        tripRepo.save(new Trip("old", "OldTrip", "Test",
                LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 10), "#DCEAF7", List.of(), List.of(), USER_ID, null));
        tripRepo.save(new Trip("new", "NewTrip", "Test",
                LocalDate.of(2025, 2, 3), LocalDate.of(2025, 2, 9), "#DCEAF7", List.of(), List.of(), USER_ID, null));
        tripRepo.save(new Trip("middle", "MiddleTrip", "Test",
                LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "#DCEAF7", List.of(), List.of(), USER_ID, null));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("new"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("middle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value("old"));
    }

    @Test
    void getAllTrips_returnsEmptyList_whenNoTripsExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void getAllTrips_returnsUnauthorized_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void getAllTrips_returnsUnauthorized_withInvalidToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", "Bearer not-a-valid-token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void getAllTrips_doesNotReturnTripsOfOtherUsers() throws Exception {
        tripRepo.save(new Trip("2", "OtherTrip", "Test",
                LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "#E3F0FC",List.of(), List.of(), OTHER_USER_ID, null));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void createTrip_savesTripForLoggedInUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Italien 2024",
                                  "description": "Rom, Florenz, Venedig",
                                  "startDate": "2024-05-12",
                                  "endDate": "2024-05-20",
                                  "coverColor": "#E3F0FC",
                                  "firstStop": {"name": "Rom", "latitude": 41.9028, "longitude": 12.4964, "countryCode": "IT", "type": "OVERNIGHT"}
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Italien 2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customTitle").value("Italien 2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].name").value("Rom"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].date").value("2024-05-12"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].type").value("OVERNIGHT"))
                // The only place of a trip lasts the whole trip
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].departureDate").value("2024-05-20"));
        assertEquals(1, tripRepo.findAllByUserIdOrderByStartDateDesc(USER_ID).size());
    }

    @Test
    void createTrip_namesTripAfterFirstStop_whenTitleIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "  ",
                                  "startDate": "2024-05-12",
                                  "endDate": "2024-05-20",
                                  "firstStop": {"name": "Rom", "latitude": 41.9028, "longitude": 12.4964, "countryCode": "IT", "type": "OVERNIGHT"}
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Rom"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customTitle").doesNotExist());

        assertNull(tripRepo.findAllByUserIdOrderByStartDateDesc(USER_ID).getFirst().title());
    }

    @Test
    void createTrip_returnsBadRequest_whenFirstStopIsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Italien", "startDate": "2024-05-12", "endDate": "2024-05-20"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Bitte einen Ort auswählen."));

        assertEquals(0, tripRepo.count());
    }

    @Test
    void createTrip_returnsFieldErrors_whenFirstStopIsInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "startDate": "2024-05-12",
                                  "endDate": "2024-05-20",
                                  "firstStop": {"name": "Rom", "latitude": 91, "longitude": 12.4964, "countryCode": "IT"}
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['firstStop.latitude']").value("Ungültige Koordinaten."));

        assertEquals(0, tripRepo.count());
    }

    @Test
    void createTrip_returnsUnauthorized_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Italien", "startDate": "2024-05-12", "endDate": "2024-05-20"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void createTrip_returnsBadRequest_whenEndDateIsBeforeStartDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Italien", "startDate": "2024-05-20", "endDate": "2024-05-12"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Das Enddatum darf nicht vor dem Startdatum liegen."));

        assertEquals(0, tripRepo.count());
    }

    @Test
    void getTrip_returnsTrip_whenTripBelongsToUser() throws Exception {
        saveTrip(USER_ID);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/1")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Italien"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").doesNotExist());
    }

    @Test
    void getTrip_returnsNotFound_whenTripBelongsToAnotherUser() throws Exception {
        saveTrip(OTHER_USER_ID);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/1")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getTrip_returnsNotFound_whenTripDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/unknown")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Reise nicht gefunden."));
    }

    @Test
    void getTrip_returnsEnglishError_whenTheUserInterfaceIsEnglish() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/unknown")
                        .header("Authorization", bearer())
                        .header("Accept-Language", "en"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Trip not found."));
    }

    @Test
    void getTrip_returnsGermanError_forALanguageWithoutOwnTexts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/unknown")
                        .header("Authorization", bearer())
                        .header("Accept-Language", "fr"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Reise nicht gefunden."));
    }

    @Test
    void updateTrip_changesTrip_whenTripBelongsToUser() throws Exception {
        saveTrip(USER_ID);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trips/1")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Italien im Mai",
                                  "description": "Rom, Florenz, Venedig",
                                  "startDate": "2024-05-10",
                                  "endDate": "2024-05-22",
                                  "coverColor": "#E0EFEA"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Italien im Mai"));

        Trip saved = tripRepo.findById("1").orElseThrow();
        assertEquals("Italien im Mai", saved.title());
        assertEquals(LocalDate.of(2024, 5, 22), saved.endDate());
        assertEquals(USER_ID, saved.userId());
    }

    @Test
    void updateTrip_returnsNotFound_whenTripBelongsToAnotherUser() throws Exception {
        saveTrip(OTHER_USER_ID);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trips/1")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Gehackt", "startDate": "2024-05-12", "endDate": "2024-05-20"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals("Italien", tripRepo.findById("1").orElseThrow().title());
    }

    @Test
    void updateTrip_removesOwnTitle_whenTitleIsEmpty() throws Exception {
        saveTrip(USER_ID);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trips/1")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "", "startDate": "2024-05-12", "endDate": "2024-05-20"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Neue Reise"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customTitle").doesNotExist());

        assertNull(tripRepo.findById("1").orElseThrow().title());
    }

    @Test
    void deleteTrip_deletesTripAndReturnsNoContent() throws Exception {
        saveTrip(USER_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/trips/1")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertTrue(tripRepo.findById("1").isEmpty());
    }

    @Test
    void deleteTrip_returnsNotFound_whenTripBelongsToAnotherUser() throws Exception {
        saveTrip(OTHER_USER_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/trips/1")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertTrue(tripRepo.findById("1").isPresent());
    }

    @Test
    void createTrip_savesTags_withoutDuplicates() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Lissabon",
                                  "startDate": "2024-10-05",
                                  "endDate": "2024-10-11",
                                  "tags": ["FOOD", "SEA", "FOOD"],
                                  "firstStop": {"name": "Lissabon", "latitude": 38.7223, "longitude": -9.1393, "countryCode": "PT", "type": "OVERNIGHT"}
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[0]").value("SEA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[1]").value("FOOD"));

        Trip saved = tripRepo.findAllByUserIdOrderByStartDateDesc(USER_ID).getFirst();
        assertEquals(List.of(TripTag.SEA, TripTag.FOOD), saved.tags());
    }

    @Test
    void createTrip_returnsBadRequest_whenTagIsUnknown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Lissabon",
                                  "startDate": "2024-10-05",
                                  "endDate": "2024-10-11",
                                  "tags": ["BEACH"]
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Die Anfrage enthält ungültige Daten."));

        assertEquals(0, tripRepo.count());
    }

    @Test
    void updateTrip_replacesTags() throws Exception {
        tripRepo.save(new Trip("1", "Italien", "Rom und Florenz",
                LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "#DCEAF7",
                List.of(TripTag.CITY_TRIP), List.of(), USER_ID, null));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trips/1")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Italien",
                                  "startDate": "2024-05-12",
                                  "endDate": "2024-05-20",
                                  "tags": ["MOUNTAINS", "FOOD"]
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[0]").value("MOUNTAINS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[1]").value("FOOD"));

        assertEquals(List.of(TripTag.MOUNTAINS, TripTag.FOOD), tripRepo.findById("1").orElseThrow().tags());
    }

    @Test
    void getTrip_returnsEmptyTags_forTripSavedWithoutTags() throws Exception {
        // Trips saved before tags existed have no tags field in MongoDB
        tripRepo.save(new Trip("1", "Italien", null,
                LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), null, null, null, USER_ID, null));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/1")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags").isEmpty());
    }

    private void saveTripWithPhotos(String coverPhotoId) {
        Stop rome = new Stop("rome", "Rom", new GeoJsonPoint(12.4964, 41.9028), "IT",
                LocalDate.of(2024, 5, 12), null, StopType.OVERNIGHT, null, List.of("p1", "p2"));
        tripRepo.save(new Trip("1", "Italien", null, LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20),
                null, List.of(), List.of(rome), USER_ID, coverPhotoId));
    }

    @Test
    void getTrip_returnsFirstPhotoAsCover_whenNoCoverIsChosen() throws Exception {
        saveTripWithPhotos(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/1")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.coverPhotoId").value("p1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].photoIds[1]").value("p2"));
    }

    @Test
    void getTrip_returnsChosenCover() throws Exception {
        saveTripWithPhotos("p2");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/1")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coverPhotoId").value("p2"));
    }

    @Test
    void getTrip_returnsFirstPhotoAsCover_whenChosenCoverNoLongerExists() throws Exception {
        saveTripWithPhotos("deleted");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/1")
                        .header("Authorization", bearer()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coverPhotoId").value("p1"));
    }

    @Test
    void setCover_savesChosenPhoto() throws Exception {
        saveTripWithPhotos(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trips/1/cover")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"photoId": "p2"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.coverPhotoId").value("p2"));

        assertEquals("p2", tripRepo.findById("1").orElseThrow().coverPhotoId());
    }

    @Test
    void setCover_withoutPhoto_makesFirstPhotoTheCover() throws Exception {
        saveTripWithPhotos("p2");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trips/1/cover")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"photoId": null}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.coverPhotoId").value("p1"));

        assertNull(tripRepo.findById("1").orElseThrow().coverPhotoId());
    }

    @Test
    void setCover_returnsBadRequest_forPhotoOfAnotherTrip() throws Exception {
        saveTripWithPhotos(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trips/1/cover")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"photoId": "foreign"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Dieses Foto gehört nicht zu dieser Reise."));

        assertNull(tripRepo.findById("1").orElseThrow().coverPhotoId());
    }

    @Test
    void setCover_returnsNotFound_forTripOfAnotherUser() throws Exception {
        saveTripWithPhotos(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/trips/1/cover")
                        .header("Authorization", "Bearer " + jwtService.createToken(OTHER_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"photoId": "p2"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
