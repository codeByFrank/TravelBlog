package org.example.backend.controller;

import org.example.backend.model.Trip;
import org.example.backend.repository.TripRepo;
import org.example.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

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
        Trip trip = new Trip("1", "TestTrip", "Test", LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "user-1");
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
                        """))
                // The owner id stays on the server
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").doesNotExist());
    }

    @Test
    void getAllTrips_returnsNewestTripFirst() throws Exception {
        tripRepo.save(new Trip("old", "OldTrip", "Test",
                LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 10), "user-1"));
        tripRepo.save(new Trip("new", "NewTrip", "Test",
                LocalDate.of(2025, 2, 3), LocalDate.of(2025, 2, 9), "user-1"));
        tripRepo.save(new Trip("middle", "MiddleTrip", "Test",
                LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "user-1"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", bearer("user-1")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("new"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("middle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value("old"));
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
                LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20), "user-2"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/trips")
                        .header("Authorization", bearer("user-1")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }
}