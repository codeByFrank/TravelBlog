package org.example.backend.service;

import org.example.backend.dto.PlaceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

// Nominatim is never called in tests: MockRestServiceServer answers instead
class PlaceServiceTest {

    private static final String SEARCH_URL = "https://nominatim.test/search";

    private MockRestServiceServer server;
    private PlaceService placeService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://nominatim.test");
        server = MockRestServiceServer.bindTo(builder).build();
        placeService = new PlaceService(builder.build());
    }

    @Test
    void search_asksForNamesInTheLanguageOfTheUserInterface() {
        server.expect(requestTo(startsWith(SEARCH_URL)))
                .andExpect(queryParam("q", "Florence"))
                .andExpect(queryParam("accept-language", "en"))
                .andRespond(withSuccess("""
                        [{"name": "Florence", "display_name": "Florence, Tuscany, Italy", "lat": "43.77", "lon": "11.25",
                          "address": {"country_code": "it"}}]
                        """, MediaType.APPLICATION_JSON));

        List<PlaceResponse> places = placeService.search("Florence", "en");

        server.verify();
        assertEquals("Florence, Tuscany, Italy", places.getFirst().displayName());
    }

    @Test
    void search_returnsPlacesWithCoordinatesAndCountryCode() {
        server.expect(requestTo(startsWith(SEARCH_URL)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("q", "Florenz"))
                .andExpect(queryParam("format", "jsonv2"))
                .andExpect(header(HttpHeaders.USER_AGENT, PlaceService.USER_AGENT))
                .andRespond(withSuccess("""
                        [
                          {
                            "place_id": 1,
                            "name": "Florenz",
                            "display_name": "Florenz, Toskana, Italien",
                            "lat": "43.7697955",
                            "lon": "11.2556404",
                            "address": {"city": "Florenz", "country": "Italien", "country_code": "it"}
                          }
                        ]
                        """, MediaType.APPLICATION_JSON));

        List<PlaceResponse> places = placeService.search(" Florenz ", "de");

        server.verify();
        assertEquals(List.of(new PlaceResponse("Florenz", "Florenz, Toskana, Italien", 43.7697955, 11.2556404, "IT")),
                places);
    }

    @Test
    void search_leavesOutResultsWithoutCountry_andUsesDisplayNameWhenNameIsEmpty() {
        server.expect(requestTo(startsWith(SEARCH_URL)))
                .andRespond(withSuccess("""
                        [
                          {"name": "Mittelmeer", "display_name": "Mittelmeer", "lat": "35.0", "lon": "18.0", "address": {}},
                          {"name": "", "display_name": "Rom, Latium, Italien", "lat": "41.9", "lon": "12.5",
                           "address": {"country_code": "it"}}
                        ]
                        """, MediaType.APPLICATION_JSON));

        List<PlaceResponse> places = placeService.search("Rom", "de");

        assertEquals(1, places.size());
        assertEquals("Rom", places.getFirst().name());
    }

    @Test
    void search_returnsEachPlaceOnlyOnce_whenNominatimFindsItTwice() {
        // e.g. the town as a point and as an administrative boundary, with slightly different coordinates
        server.expect(requestTo(startsWith(SEARCH_URL)))
                .andExpect(queryParam("limit", "10"))
                .andRespond(withSuccess("""
                        [
                          {"name": "Straßburg", "display_name": "Straßburg, Grand Est, Frankreich",
                           "lat": "48.5846", "lon": "7.7507", "address": {"country_code": "fr"}},
                          {"name": "Strasburg", "display_name": "Strasburg, Mecklenburg-Vorpommern, Deutschland",
                           "lat": "53.5106", "lon": "13.7442", "address": {"country_code": "de"}},
                          {"name": "Straßburg", "display_name": "Straßburg, Grand Est, Frankreich",
                           "lat": "48.5734", "lon": "7.7521", "address": {"country_code": "fr"}}
                        ]
                        """, MediaType.APPLICATION_JSON));

        List<PlaceResponse> places = placeService.search("Strasburg", "de");

        assertEquals(List.of("Straßburg, Grand Est, Frankreich", "Strasburg, Mecklenburg-Vorpommern, Deutschland"),
                places.stream().map(PlaceResponse::displayName).toList());
        assertEquals(48.5846, places.getFirst().latitude());
    }

    @Test
    void search_returnsEmptyList_whenNothingIsFound() {
        server.expect(requestTo(startsWith(SEARCH_URL)))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertTrue(placeService.search("xyzxyz", "de").isEmpty());
    }

    @Test
    void search_throwsServiceUnavailable_whenNominatimFails() {
        server.expect(requestTo(startsWith(SEARCH_URL)))
                .andRespond(withServerError());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> placeService.search("Florenz", "de"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    }

    @Test
    void search_throwsBadRequest_withoutCallingNominatim_whenQueryIsBlank() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> placeService.search("   ", "de"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        server.verify();
    }
}
