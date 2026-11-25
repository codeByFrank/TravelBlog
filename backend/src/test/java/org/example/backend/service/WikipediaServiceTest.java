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

    // The German Wikipedia; the base URL has a {language} part like in application.properties
    private static final String API_URL = "https://de.wikipedia.test/w/api.php";
    private static final String ENGLISH_API_URL = "https://en.wikipedia.test/w/api.php";
    // Florence
    private static final double LATITUDE = 43.77;
    private static final double LONGITUDE = 11.26;
    // An article about Florence, near the place of the tests
    private static final String FLORENCE_PAGE = """
            {"query": {"pages": [{
              "title": "Florenz",
              "extract": "Florenz ist die Hauptstadt der Toskana.",
              "coordinates": [{"lat": 43.7714, "lon": 11.2542}],
              "fullurl": "https://de.wikipedia.org/wiki/Florenz"
            }]}}
            """;

    private MockRestServiceServer server;
    private WikipediaService wikipediaService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://{language}.wikipedia.test");
        server = MockRestServiceServer.bindTo(builder).build();
        wikipediaService = new WikipediaService(builder.build());
    }

    // The German Wikipedia answers with the page; it is no article near the place, so the English Wikipedia is asked
    // next, and it has no article with that name either
    private void respondWith(String page) {
        server.expect(requestTo(startsWith(API_URL)))
                .andRespond(withSuccess("{\"query\": {\"pages\": [" + page + "]}}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(startsWith(ENGLISH_API_URL)))
                .andRespond(withSuccess("{\"query\": {\"pages\": [{\"title\": \"x\", \"missing\": true}]}}",
                        MediaType.APPLICATION_JSON));
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

        Optional<WikipediaResponse> article = wikipediaService.find(" Florenz ", LATITUDE, LONGITUDE, "de");

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

        assertTrue(wikipediaService.find("Florenz", LATITUDE, LONGITUDE, "de").isEmpty());
    }

    @Test
    void find_returnsEmpty_forDisambiguationPage() {
        respondWith("""
                {"title": "Florenz", "extract": "Florenz steht für:", "pageprops": {"disambiguation": ""},
                 "coordinates": [{"lat": 43.77, "lon": 11.25}], "fullurl": "https://de.wikipedia.org/wiki/Florenz"}
                """);

        assertTrue(wikipediaService.find("Florenz", LATITUDE, LONGITUDE, "de").isEmpty());
    }

    @Test
    void find_returnsEmpty_forMissingArticleOrArticleWithoutCoordinates() {
        respondWith("""
                {"title": "Florenz (Altstadt)", "missing": true}
                """);
        assertTrue(wikipediaService.find("Florenz (Altstadt)", LATITUDE, LONGITUDE, "de").isEmpty());

        server.reset();
        respondWith("""
                {"title": "Florenz", "extract": "Text", "fullurl": "https://de.wikipedia.org/wiki/Florenz"}
                """);
        assertTrue(wikipediaService.find("Florenz", LATITUDE, LONGITUDE, "de").isEmpty());
    }

    @Test
    void find_asksTheEnglishWikipedia_forEnglish() {
        server.expect(requestTo(startsWith(ENGLISH_API_URL)))
                .andExpect(queryParam("titles", "Florence"))
                .andRespond(withSuccess("""
                        {"query": {"pages": [{
                          "title": "Florence",
                          "extract": "Florence is the capital city of Tuscany.",
                          "coordinates": [{"lat": 43.7714, "lon": 11.2542}],
                          "fullurl": "https://en.wikipedia.org/wiki/Florence"
                        }]}}
                        """, MediaType.APPLICATION_JSON));

        Optional<WikipediaResponse> article = wikipediaService.find("Florence", LATITUDE, LONGITUDE, "en");

        server.verify();
        assertEquals(Optional.of(new WikipediaResponse("Florence", "Florence is the capital city of Tuscany.",
                "https://en.wikipedia.org/wiki/Florence")), article);
    }

    @Test
    void find_followsTheLinkToTheEnglishArticle_whenThePlaceHasAGermanName() {
        // A stop saved with the German place search is called "Venedig"; the English Wikipedia has no such article
        server.expect(requestTo(startsWith(ENGLISH_API_URL)))
                .andExpect(queryParam("titles", "Venedig"))
                .andRespond(withSuccess("{\"query\": {\"pages\": [{\"title\": \"Venedig\", \"missing\": true}]}}",
                        MediaType.APPLICATION_JSON));
        // The German article links to the English one
        server.expect(requestTo(startsWith(API_URL)))
                .andExpect(queryParam("titles", "Venedig"))
                .andExpect(queryParam("lllang", "en"))
                .andRespond(withSuccess("""
                        {"query": {"pages": [{
                          "title": "Venedig",
                          "extract": "Venedig ist eine Stadt in Italien.",
                          "coordinates": [{"lat": 45.4375, "lon": 12.3358}],
                          "fullurl": "https://de.wikipedia.org/wiki/Venedig",
                          "langlinks": [{"lang": "en", "title": "Venice"}]
                        }]}}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(startsWith(ENGLISH_API_URL)))
                .andExpect(queryParam("titles", "Venice"))
                .andRespond(withSuccess("""
                        {"query": {"pages": [{
                          "title": "Venice",
                          "extract": "Venice is a city in northeastern Italy.",
                          "coordinates": [{"lat": 45.4375, "lon": 12.3358}],
                          "fullurl": "https://en.wikipedia.org/wiki/Venice"
                        }]}}
                        """, MediaType.APPLICATION_JSON));

        Optional<WikipediaResponse> article = wikipediaService.find("Venedig", 45.44, 12.33, "en");

        server.verify();
        assertEquals(Optional.of(new WikipediaResponse("Venice", "Venice is a city in northeastern Italy.",
                "https://en.wikipedia.org/wiki/Venice")), article);
    }

    @Test
    void find_throwsBadRequest_whenNameIsEmpty() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> wikipediaService.find("  ", LATITUDE, LONGITUDE, "de"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void find_throwsServiceUnavailable_whenWikipediaFails() {
        server.expect(requestTo(startsWith(API_URL))).andRespond(withServerError());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> wikipediaService.find("Florenz", LATITUDE, LONGITUDE, "de"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    }

    @Test
    void find_asksWikipediaOnlyOnce_forTheSamePlace() {
        // Only one request is expected: the second find must use the remembered answer
        server.expect(requestTo(startsWith(API_URL)))
                .andRespond(withSuccess(FLORENCE_PAGE, MediaType.APPLICATION_JSON));

        Optional<WikipediaResponse> first = wikipediaService.find("Florenz", LATITUDE, LONGITUDE, "de");
        Optional<WikipediaResponse> second = wikipediaService.find("Florenz", LATITUDE, LONGITUDE, "de");

        server.verify();
        assertEquals(first, second);
        assertTrue(second.isPresent());
    }

    @Test
    void find_remembersThatThereIsNoArticle() {
        respondWith("""
                {"title": "Florenz (Altstadt)", "missing": true}
                """);

        assertTrue(wikipediaService.find("Florenz (Altstadt)", LATITUDE, LONGITUDE, "de").isEmpty());
        // No further request: MockRestServiceServer would fail on an unexpected one
        assertTrue(wikipediaService.find("Florenz (Altstadt)", LATITUDE, LONGITUDE, "de").isEmpty());
        server.verify();
    }

    @Test
    void find_asksAgain_afterWikipediaFailed() {
        // An error is not remembered, so the next page view gets the article
        server.expect(requestTo(startsWith(API_URL))).andRespond(withServerError());
        server.expect(requestTo(startsWith(API_URL)))
                .andRespond(withSuccess(FLORENCE_PAGE, MediaType.APPLICATION_JSON));

        assertThrows(ResponseStatusException.class,
                () -> wikipediaService.find("Florenz", LATITUDE, LONGITUDE, "de"));
        Optional<WikipediaResponse> article = wikipediaService.find("Florenz", LATITUDE, LONGITUDE, "de");

        server.verify();
        assertTrue(article.isPresent());
    }
}
