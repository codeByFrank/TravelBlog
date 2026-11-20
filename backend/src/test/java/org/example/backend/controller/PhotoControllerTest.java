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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
class PhotoControllerTest {

    private static final String USER_ID = "user-1";
    private static final String OTHER_USER_ID = "user-2";
    private static final String TRIP_ID = "1";
    private static final String PHOTOS_URL = "/api/trips/1/stops/rome/photos";
    private static final byte[] PHOTO_BYTES = {1, 2, 3};

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TripRepo tripRepo;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @BeforeEach
    void setUp() {
        tripRepo.deleteAll();
        gridFsTemplate.delete(new Query());
    }

    private String bearer(String userId) {
        return "Bearer " + jwtService.createToken(userId);
    }

    private static MockMultipartFile jpeg() {
        return new MockMultipartFile("file", "rom.jpg", "image/jpeg", PHOTO_BYTES);
    }

    private void saveTrip(String userId, List<String> photoIds) {
        Stop rome = new Stop("rome", "Rom", new GeoJsonPoint(12.4964, 41.9028), "IT",
                LocalDate.of(2024, 5, 12), null, true, photoIds);
        tripRepo.save(new Trip(TRIP_ID, "Italien", null, LocalDate.of(2024, 5, 12), LocalDate.of(2024, 5, 20),
                null, List.of(), List.of(rome), userId, null));
    }

    private List<String> photoIdsOfRome() {
        return tripRepo.findById(TRIP_ID).orElseThrow().stops().getFirst().photoIds();
    }

    private boolean noFilesStored() {
        return gridFsTemplate.find(new Query()).first() == null;
    }

    @Test
    void addPhoto_storesFileAndAddsItToStop() throws Exception {
        saveTrip(USER_ID, List.of());

        mockMvc.perform(MockMvcRequestBuilders.multipart(PHOTOS_URL).file(jpeg())
                        .header(HttpHeaders.AUTHORIZATION, bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stops[0].photoIds.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coverPhotoId").isNotEmpty());

        assertEquals(1, photoIdsOfRome().size());
    }

    @Test
    void addPhoto_returnsBadRequest_whenStopHasFivePhotos() throws Exception {
        saveTrip(USER_ID, List.of("p1", "p2", "p3", "p4", "p5"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(PHOTOS_URL).file(jpeg())
                        .header(HttpHeaders.AUTHORIZATION, bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Pro Ort sind höchstens 5 Fotos möglich."));

        assertEquals(5, photoIdsOfRome().size());
    }

    @Test
    void addPhoto_returnsBadRequest_whenFileIsNoImage() throws Exception {
        saveTrip(USER_ID, List.of());
        MockMultipartFile text = new MockMultipartFile("file", "notiz.txt", "text/plain", PHOTO_BYTES);

        mockMvc.perform(MockMvcRequestBuilders.multipart(PHOTOS_URL).file(text)
                        .header(HttpHeaders.AUTHORIZATION, bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertEquals(0, photoIdsOfRome().size());
    }

    @Test
    void addPhoto_returnsNotFound_whenTripBelongsToAnotherUser() throws Exception {
        saveTrip(OTHER_USER_ID, List.of());

        mockMvc.perform(MockMvcRequestBuilders.multipart(PHOTOS_URL).file(jpeg())
                        .header(HttpHeaders.AUTHORIZATION, bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertNull(gridFsTemplate.find(new Query()).first());
    }

    @Test
    void addPhoto_returnsNotFound_whenStopDoesNotExist() throws Exception {
        saveTrip(USER_ID, List.of());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/trips/1/stops/unknown/photos").file(jpeg())
                        .header(HttpHeaders.AUTHORIZATION, bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void addPhoto_returnsUnauthorized_withoutToken() throws Exception {
        saveTrip(USER_ID, List.of());

        mockMvc.perform(MockMvcRequestBuilders.multipart(PHOTOS_URL).file(jpeg()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}