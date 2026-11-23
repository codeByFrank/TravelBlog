package org.example.backend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.example.backend.dto.WikipediaResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Finds the German Wikipedia article about a place, e.g. "Florenz", and returns its first sentences
 * (see architecture.md → Wikipedia).
 */
@Service
public class WikipediaService {

    // Wikipedia asks API clients to name themselves; the same text as for Nominatim
    static final String USER_AGENT = PlaceService.USER_AGENT;
    // An article whose coordinates are farther away is about another place with the same name
    static final double MAX_DISTANCE_KM = 50;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int SENTENCES = 3;

    private final RestClient wikipediaRestClient;

    public WikipediaService(@Qualifier("wikipediaRestClient") RestClient wikipediaRestClient) {
        this.wikipediaRestClient = wikipediaRestClient;
    }

    // The article with this title (after redirects) if it is about a place near the given coordinates
    // empty for missing articles, disambiguation pages, articles without coordinates and places far away
    public Optional<WikipediaResponse> find(String name, double latitude, double longitude) {
        String title = name == null ? "" : name.trim();
        if (title.isEmpty() || title.length() > MAX_NAME_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungültiger Ortsname.");
        }

        QueryResult result;
        try {
            result = wikipediaRestClient.get()
                    .uri(uri -> uri.path("/w/api.php")
                            .queryParam("action", "query")
                            .queryParam("format", "json")
                            .queryParam("formatversion", 2)
                            .queryParam("redirects", 1)
                            .queryParam("titles", title)
                            .queryParam("prop", "extracts|coordinates|pageprops|info")
                            .queryParam("exintro", 1)
                            .queryParam("explaintext", 1)
                            .queryParam("exsentences", SENTENCES)
                            .queryParam("ppprop", "disambiguation")
                            .queryParam("inprop", "url")
                            .build())
                    .header(HttpHeaders.USER_AGENT, USER_AGENT)
                    .retrieve()
                    .body(QueryResult.class);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Wikipedia ist gerade nicht erreichbar.", e);
        }

        if (result == null || result.query() == null || result.query().pages() == null) {
            return Optional.empty();
        }
        GeoJsonPoint place = new GeoJsonPoint(longitude, latitude);
        return result.query().pages().stream()
                .filter(page -> isArticleNear(page, place))
                .findFirst()
                .map(page -> new WikipediaResponse(page.title(), page.extract().trim(), page.fullurl()));
    }

    private static boolean isArticleNear(Page page, GeoJsonPoint place) {
        boolean isArticle = !Boolean.TRUE.equals(page.missing())
                && (page.pageprops() == null || !page.pageprops().containsKey("disambiguation"))
                && page.extract() != null && !page.extract().isBlank()
                && page.fullurl() != null;
        if (!isArticle || page.coordinates() == null || page.coordinates().isEmpty()) {
            return false;
        }
        Coordinate coordinate = page.coordinates().getFirst();
        GeoJsonPoint article = new GeoJsonPoint(coordinate.lon(), coordinate.lat());
        return RouteLengthCalculator.distanceKm(place, article) <= MAX_DISTANCE_KM;
    }

    // The parts of the answer (format=json, formatversion=2) that are used
    @JsonIgnoreProperties(ignoreUnknown = true)
    record QueryResult(Query query) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Query(List<Page> pages) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Page(String title,
                Boolean missing,
                String extract,
                List<Coordinate> coordinates,
                Map<String, String> pageprops,
                String fullurl) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Coordinate(double lat, double lon) {
    }
}
