package org.example.backend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.backend.dto.PlaceResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
    private static final int MAX_QUERY_LENGTH = 100;

    private final RestClient nominatimRestClient;

    public PlaceService(RestClient nominatimRestClient) {
        this.nominatimRestClient = nominatimRestClient;
    }

    public List<PlaceResponse> search(String query) {
        String text = query == null ? "" : query.trim();
        if (text.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bitte einen Ort eingeben.");
        }
        if (text.length() > MAX_QUERY_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Suchbegriff ist zu lang.");
        }

        NominatimPlace[] places;
        try {
            places = nominatimRestClient.get()
                    .uri(uri -> uri.path("/search")
                            .queryParam("q", text)
                            .queryParam("format", "jsonv2")
                            .queryParam("addressdetails", 1)
                            .queryParam("limit", MAX_RESULTS)
                            .queryParam("accept-language", "de")
                            .build())
                    .header(HttpHeaders.USER_AGENT, USER_AGENT)
                    .retrieve()
                    .body(NominatimPlace[].class);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Die Ortssuche ist gerade nicht erreichbar. Bitte versuche es später noch einmal.", e);
        }

        if (places == null) {
            return List.of();
        }
        return Arrays.stream(places)
                .map(PlaceService::toResponse)
                .filter(Objects::nonNull)
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
