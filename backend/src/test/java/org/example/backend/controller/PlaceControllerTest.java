package org.example.backend.controller;

import org.example.backend.dto.PlaceResponse;
import org.example.backend.service.JwtService;
import org.example.backend.service.PlaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    // The calls to Nominatim are tested in PlaceServiceTest; here only the endpoint
    @MockitoBean
    private PlaceService placeService;

    @Test
    void searchPlaces_returnsPlacesFromTheService() throws Exception {
        when(placeService.search("Florenz")).thenReturn(List.of(
                new PlaceResponse("Florenz", "Florenz, Toskana, Italien", 43.77, 11.26, "IT")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/places").param("query", "Florenz")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken("user-1")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Florenz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].latitude").value(43.77))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].countryCode").value("IT"));
    }

    @Test
    void searchPlaces_returnsUnauthorized_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/places").param("query", "Florenz"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
