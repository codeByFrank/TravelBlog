package org.example.backend.controller;

import org.example.backend.model.Stop;
import org.example.backend.model.Trip;
import org.example.backend.repository.TripRepo;
import org.example.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class StopControllerTest {

    private static final String USER_ID = "user-1";
    private static final String OTHER_USER_ID = "user-2";
    private static final String TRIP_ID = "1";
    private static final String STOPS_URL = "/api/trips/1/stops";

    // Trip from 12 to 20 May 2024
    private static final String FLORENCE_JSON = """
            {
              "name": "Florenz",
              "latitude": 43.7696,
              "longitude": 11.2558,
              "countryCode": "IT",
              "date": "2024-05-15",
              "note": "Bestes Eis der Reise",
              "overnight": true
            }
            """;

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

    private String bearer() {
        return "Bearer " + jwtService.createToken(USER_ID);
    }

    private void saveTrip(String userId, List<Stop> stops) {
        tripRepo.save(new Trip(TRIP_ID, "Italien", null,
                LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), null, List.of(), stops, userId, null));
    }

    private static Stop rome() {
        return new Stop("rome", "Rom", new GeoJsonPoint(12.4964, 41.9028), "IT", LocalDate.of(2024, 5, 12), null, true, List.of());
    }

    private static Stop venice() {
        return new Stop("venice", "Venedig", new GeoJsonPoint(12.3155, 45.4408), "IT", LocalDate.of(2024, 5, 18), null, null, List.of());
    }

    @Test
    void addStop_savesStopAndReturnsTrip() throws Exception {
        saveTrip(USER_ID, List.of());

        mockMvc.perform(MockMvcRequestBuilders.post(STOPS_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FLORENCE_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].name").value("Florenz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].latitude").value(43.7696))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].longitude").value(11.2558))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].countryCode").value("IT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].note").value("Bestes Eis der Reise"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].overnight").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.routeLengthKm").value(0));

        Stop saved = tripRepo.findById(TRIP_ID).orElseThrow().stops().getFirst();
        // Stored as GeoJSON: x is the longitude, y the latitude
        assertEquals(11.2558, saved.location().getX());
        assertEquals(43.7696, saved.location().getY());
    }

    @Test
    void addStop_sortsStopsByDate_andCalculatesDistances() throws Exception {
        saveTrip(USER_ID, List.of(rome(), venice()));

        // Florence (15 May) goes between Rome (12 May) and Venice (18 May)
        mockMvc.perform(MockMvcRequestBuilders.post(STOPS_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FLORENCE_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].name").value("Rom"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[1].name").value("Florenz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[2].name").value("Venedig"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].distanceFromPreviousKm").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[1].distanceFromPreviousKm").value(231))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[2].distanceFromPreviousKm").value(204))
                .andExpect(MockMvcResultMatchers.jsonPath("$.routeLengthKm").value(435));
    }

    @Test
    void addStop_returnsBadRequest_whenDateIsOutsideTheTrip() throws Exception {
        saveTrip(USER_ID, List.of());

        mockMvc.perform(MockMvcRequestBuilders.post(STOPS_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FLORENCE_JSON.replace("2024-05-15", "2024-05-21")))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Das Datum muss innerhalb der Reise liegen."));

        assertEquals(0, tripRepo.findById(TRIP_ID).orElseThrow().stops().size());
    }

    @Test
    void addStop_returnsFieldErrors_whenDataIsInvalid() throws Exception {
        saveTrip(USER_ID, List.of());

        mockMvc.perform(MockMvcRequestBuilders.post(STOPS_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "latitude": 91, "longitude": 11.2, "countryCode": "Italien", "date": "2024-05-15"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.name").value("Bitte einen Namen eingeben."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.latitude").value("Ungültige Koordinaten."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.countryCode").value("Ungültiger Ländercode."));
    }

    @Test
    void addStop_returnsNotFound_whenTripBelongsToAnotherUser() throws Exception {
        saveTrip(OTHER_USER_ID, List.of());

        mockMvc.perform(MockMvcRequestBuilders.post(STOPS_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FLORENCE_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals(0, tripRepo.findById(TRIP_ID).orElseThrow().stops().size());
    }

    @Test
    void addStop_isNoOvernightStay_whenFieldIsMissing() throws Exception {
        saveTrip(USER_ID, List.of());

        mockMvc.perform(MockMvcRequestBuilders.post(STOPS_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Pisa", "latitude": 43.72, "longitude": 10.40, "countryCode": "IT", "date": "2024-05-16"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].overnight").value(false));
    }

    @Test
    void getTrip_returnsOvernightFalse_forStopSavedWithoutTheField() throws Exception {
        // venice() has overnight = null, like stops saved before the field existed
        saveTrip(USER_ID, List.of(rome(), venice()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].overnight").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[1].overnight").value(false));
    }

    @Test
    void addStop_returnsUnauthorized_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(STOPS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FLORENCE_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void addStop_worksForTripSavedWithoutStops() throws Exception {
        // Trips saved before stops existed have no stops field in MongoDB
        saveTrip(USER_ID, null);

        mockMvc.perform(MockMvcRequestBuilders.post(STOPS_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FLORENCE_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops.length()").value(1));
    }

    @Test
    void updateStop_changesStop_andKeepsItsId() throws Exception {
        saveTrip(USER_ID, List.of(rome(), venice()));

        // Rome becomes Florence on 19 May, so it moves behind Venice
        mockMvc.perform(MockMvcRequestBuilders.put(STOPS_URL + "/rome")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FLORENCE_JSON.replace("2024-05-15", "2024-05-19")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].name").value("Venedig"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[1].id").value("rome"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[1].name").value("Florenz"));
    }

    @Test
    void updateStop_returnsNotFound_whenStopDoesNotExist() throws Exception {
        saveTrip(USER_ID, List.of(rome()));

        mockMvc.perform(MockMvcRequestBuilders.put(STOPS_URL + "/unknown")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FLORENCE_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Ort nicht gefunden."));
    }

    @Test
    void deleteStop_removesStop() throws Exception {
        saveTrip(USER_ID, List.of(rome(), venice()));

        mockMvc.perform(MockMvcRequestBuilders.delete(STOPS_URL + "/rome")
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].name").value("Venedig"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.routeLengthKm").value(0));

        assertEquals(List.of("venice"),
                tripRepo.findById(TRIP_ID).orElseThrow().stops().stream().map(Stop::id).toList());
    }

    @Test
    void deleteStop_returnsBadRequest_whenItIsTheLastStop() throws Exception {
        saveTrip(USER_ID, List.of(rome()));

        mockMvc.perform(MockMvcRequestBuilders.delete(STOPS_URL + "/rome")
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Eine Reise braucht mindestens einen Ort. Ändere ihn stattdessen."));

        assertEquals(1, tripRepo.findById(TRIP_ID).orElseThrow().stops().size());
    }

    @Test
    void deleteStop_returnsNotFound_whenTripBelongsToAnotherUser() throws Exception {
        saveTrip(OTHER_USER_ID, List.of(rome()));

        mockMvc.perform(MockMvcRequestBuilders.delete(STOPS_URL + "/rome")
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals(1, tripRepo.findById(TRIP_ID).orElseThrow().stops().size());
    }

    @Test
    void updateTrip_returnsBadRequest_whenStopsWouldLieOutsideTheNewDates() throws Exception {
        saveTrip(USER_ID, List.of(rome(), venice()));

        // Venice (18 May) would be after the new end date
        mockMvc.perform(MockMvcRequestBuilders.put("/api/trips/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Italien", "startDate": "2024-05-12", "endDate": "2024-05-16"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Einige Orte liegen außerhalb des neuen Zeitraums. Ändere zuerst ihr Datum."));

        assertEquals(LocalDate.of(2024, 5, 20), tripRepo.findById(TRIP_ID).orElseThrow().endDate());
    }

    @Test
    void getTrip_returnsStops_withoutDistanceForTheFirstStop() throws Exception {
        saveTrip(USER_ID, List.of(rome()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips/1")
                        .header(HttpHeaders.AUTHORIZATION, bearer()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].name").value("Rom"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].distanceFromPreviousKm").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.routeLengthKm").value(0));
    }

    @Test
    void updateStop_keepsPhotos() throws Exception {
        saveTrip(USER_ID, List.of(rome().withPhotoIds(List.of("p1"))));

        mockMvc.perform(MockMvcRequestBuilders.put(STOPS_URL + "/rome")
                        .header(HttpHeaders.AUTHORIZATION, bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FLORENCE_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].photoIds[0]").value("p1"));
    }
}
