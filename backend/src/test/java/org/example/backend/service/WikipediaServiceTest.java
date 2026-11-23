package org.example.backend.service;

import org.example.backend.dto.WikipediaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

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

// Wikipedia is never called in tests: MockRestServiceServer answers instead
class WikipediaServiceTest {

    private static final String API_URL = "https://wikipedia.test/w/api.php";
    // Florence
    private static final double LATITUDE = 43.77;
    private static final double LONGITUDE = 11.26;

    private MockRestServiceServer server;
    private WikipediaService wikipediaService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://wikipedia.test");
        server = MockRestServiceServer.bindTo(builder).build();
        wikipediaService = new WikipediaService(builder.build());
    }

    private void respondWith(String page) {
        server.expect(requestTo(startsWith(API_URL)))
                .andRespond(withSuccess("{\"query\": {\"pages\": [" + page + "]}}", MediaType.APPLICATION_JSON));
    }

    @Test
    void find_returnsTitleExtractAndUrl_forArticleNearThePlace() {
        server.expect(requestTo(startsWith(API_URL)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("titles", "Florenz"))
                .andExpect(queryParam("redirects", "1"))
                .andExpect(header(HttpHeaders.USER_AGENT, WikipediaService.USER_AGENT))
                .andRespond(withSuccess("""
                        {"query": {"pages": [{
                          "title": "Florenz",
                          "extract": "Florenz ist die Hauptstadt der Toskana. ",
                          "coordinates": [{"lat": 43.7714, "lon": 11.2542}],
                          "fullurl": "https://de.wikipedia.org/wiki/Florenz"
                        }]}}
                        """, MediaType.APPLICATION_JSON));

        Optional<WikipediaResponse> article = wikipediaService.find(" Florenz ", LATITUDE, LONGITUDE);

        server.verify();
        assertEquals(Optional.of(new WikipediaResponse("Florenz", "Florenz ist die Hauptstadt der Toskana.",
                "https://de.wikipedia.org/wiki/Florenz")), article);
    }

    @Test
    void find_returnsEmpty_whenArticleIsAboutAPlaceFarAway() {
        // Florence, South Carolina
        respondWith("""
                {"title": "Florenz", "extract": "Florenz ist eine Stadt in South Carolina.",
                 "coordinates": [{"lat": 34.19, "lon": -79.76}], "fullurl": "https://de.wikipedia.org/wiki/Florenz"}
                """);

        assertTrue(wikipediaService.find("Florenz", LATITUDE, LONGITUDE).isEmpty());
    }

    @Test
    void find_returnsEmpty_forDisambiguationPage() {
        respondWith("""
                {"title": "Florenz", "extract": "Florenz steht für:", "pageprops": {"disambiguation": ""},
                 "coordinates": [{"lat": 43.77, "lon": 11.25}], "fullurl": "https://de.wikipedia.org/wiki/Florenz"}
                """);

        assertTrue(wikipediaService.find("Florenz", LATITUDE, LONGITUDE).isEmpty());
    }

    @Test
    void find_returnsEmpty_forMissingArticleOrArticleWithoutCoordinates() {
        respondWith("""
                {"title": "Florenz (Altstadt)", "missing": true}
                """);
        assertTrue(wikipediaService.find("Florenz (Altstadt)", LATITUDE, LONGITUDE).isEmpty());

        server.reset();
        respondWith("""
                {"title": "Florenz", "extract": "Text", "fullurl": "https://de.wikipedia.org/wiki/Florenz"}
                """);
        assertTrue(wikipediaService.find("Florenz", LATITUDE, LONGITUDE).isEmpty());
    }

    @Test
    void find_throwsBadRequest_whenNameIsEmpty() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> wikipediaService.find("  ", LATITUDE, LONGITUDE));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void find_throwsServiceUnavailable_whenWikipediaFails() {
        server.expect(requestTo(startsWith(API_URL))).andRespond(withServerError());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> wikipediaService.find("Florenz", LATITUDE, LONGITUDE));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    }
}
