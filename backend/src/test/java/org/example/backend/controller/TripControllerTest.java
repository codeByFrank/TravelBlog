package org.example.backend.controller;

import org.example.backend.model.Trip;
import org.example.backend.repository.TripRepo;
import org.example.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class TripControllerTest {

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

    private String bearer(String userId) {
        return "Bearer " + jwtService.createToken(userId);
    }

    @Test
    void getAllTrips_returnsSavedTrip() throws Exception {
        Trip trip = new Trip("1", "TestTrip", "Test", LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "#E3F0FC","user-1");
        tripRepo.save(trip);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", bearer("user-1")))
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
                        """));
    }

    @Test
    void getAllTrips_returnsEmptyList_whenNoTripsExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", bearer("user-1")))
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
                LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "#E3F0FC","user-2"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", bearer("user-1")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void createTrip_savesTripForLoggedInUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .header("Authorization", bearer("user-1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Italien 2024",
                                  "description": "Rom, Florenz, Venedig",
                                  "startDate": "2024-05-12",
                                  "endDate": "2024-05-20",
                                  "coverColor": "#E3F0FC"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Italien 2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("user-1"));

        assertEquals(1, tripRepo.findAllByUserId("user-1").size());
    }

    @Test
    void createTrip_returnsBadRequest_whenTitleIsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/trips")
                        .header("Authorization", bearer("user-1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "startDate": "2024-05-12",
                                  "endDate": "2024-05-20"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.title").value("Bitte einen Titel eingeben."));

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
                        .header("Authorization", bearer("user-1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Italien", "startDate": "2024-05-20", "endDate": "2024-05-12"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Das Enddatum darf nicht vor dem Startdatum liegen."));

        assertEquals(0, tripRepo.count());
    }
}