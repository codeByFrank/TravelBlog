package org.example.backend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.backend.dto.PlaceResponse;
import org.example.backend.exception.LocalizedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Searches places by name with Nominatim and returns name, coordinates and country code.
 * Nominatim allows at most one request per second and needs a User-Agent that names the application,
 * so the frontend sends a search only when the user submits it.
 */
@Service
public class PlaceService {

    static final String USER_AGENT = "TravelBlog/1.0 (Java + React course project)";
    private static final int MAX_RESULTS = 5;
    // Nominatim often returns the same place twice (e.g. the town and its boundary), so more are requested
    private static final int REQUESTED_RESULTS = 10;
    private static final int MAX_QUERY_LENGTH = 100;

    private final RestClient nominatimRestClient;

    // Named, because there is a second RestClient for Wikipedia
    public PlaceService(@Qualifier("nominatimRestClient") RestClient nominatimRestClient) {
        this.nominatimRestClient = nominatimRestClient;
    }

    // language: "de" or "en" (UserLanguage); the names of the places and countries come in this language
    public List<PlaceResponse> search(String query, String language) {
        String text = query == null ? "" : query.trim();
        if (text.isEmpty()) {
            throw new LocalizedException(HttpStatus.BAD_REQUEST, "error.places.queryRequired");
        }
        if (text.length() > MAX_QUERY_LENGTH) {
            throw new LocalizedException(HttpStatus.BAD_REQUEST, "error.places.queryTooLong");
        }

        NominatimPlace[] places;
        try {
            places = nominatimRestClient.get()
                    .uri(uri -> uri.path("/search")
                            .queryParam("q", text)
                            .queryParam("format", "jsonv2")
                            .queryParam("addressdetails", 1)
                            .queryParam("limit", REQUESTED_RESULTS)
                            .queryParam("accept-language", language)
                            .build())
                    .header(HttpHeaders.USER_AGENT, USER_AGENT)
                    .retrieve()
                    .body(NominatimPlace[].class);
        } catch (RestClientException e) {
            throw new LocalizedException(HttpStatus.SERVICE_UNAVAILABLE,
                    "error.places.unavailable", e);
        }

        if (places == null) {
            return List.of();
        }
        // Same full name = same place for the user; the first one is kept, because Nominatim sorts by importance
        Map<String, PlaceResponse> byDisplayName = new LinkedHashMap<>();
        Arrays.stream(places)
                .map(PlaceService::toResponse)
                .filter(Objects::nonNull)
                .forEach(place -> byDisplayName.putIfAbsent(place.displayName(), place));
        return byDisplayName.values().stream()
                .limit(MAX_RESULTS)
                .toList();
    }

    // Results without a country (e.g. seas) cannot be stops and are left out
    private static PlaceResponse toResponse(NominatimPlace place) {
        if (place.address() == null || place.address().countryCode() == null
                || place.lat() == null || place.lon() == null) {
            return null;
        }
        String displayName = place.displayName() == null ? "" : place.displayName();
        String name = place.name() == null || place.name().isBlank()
                ? displayName.split(",")[0].trim()
                : place.name();
        return new PlaceResponse(name,
                displayName,
                Double.parseDouble(place.lat()),
                Double.parseDouble(place.lon()),
                place.address().countryCode().toUpperCase(Locale.ROOT));
    }

    // The parts of a Nominatim result (format=jsonv2) that are used; coordinates come as strings
    @JsonIgnoreProperties(ignoreUnknown = true)
    record NominatimPlace(String name,
                          @JsonProperty("display_name") String displayName,
                          String lat,
                          String lon,
                          NominatimAddress address) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record NominatimAddress(@JsonProperty("country_code") String countryCode) {
    }
}
