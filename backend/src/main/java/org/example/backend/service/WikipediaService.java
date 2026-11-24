package org.example.backend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.example.backend.dto.WikipediaResponse;
import org.example.backend.exception.LocalizedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Finds the Wikipedia article about a place in the user's language, e.g. "Florenz" in the German or "Florence" in
 * the English Wikipedia, and returns its first sentences
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

    // The article with this title (after redirects) if it is about a place near the given coordinates;
    // empty for missing articles, disambiguation pages, articles without coordinates and places far away.
    // language: "de" or "en" (UserLanguage), the Wikipedia that is asked, e.g. https://en.wikipedia.org.
    // A place keeps the name it got when the stop was saved, e.g. "Venedig" from the German place search.
    // If the user's Wikipedia has no article with that name, the other Wikipedia is asked for it, and its link
    // to the article in the user's language is followed: "Venedig" (German) → "Venice" (English).
    public Optional<WikipediaResponse> find(String name, double latitude, double longitude, String language) {
        String title = name == null ? "" : name.trim();
        if (title.isEmpty() || title.length() > MAX_NAME_LENGTH) {
            throw new LocalizedException(HttpStatus.BAD_REQUEST, "error.wikipedia.invalidName");
        }
        GeoJsonPoint place = new GeoJsonPoint(longitude, latitude);

        Optional<Page> article = findArticle(title, language, place);
        if (article.isEmpty()) {
            String otherLanguage = UserLanguage.ENGLISH.equals(language) ? UserLanguage.GERMAN : UserLanguage.ENGLISH;
            article = findArticle(title, otherLanguage, place)
                    .flatMap(otherArticle -> titleIn(otherArticle, language))
                    .flatMap(translatedTitle -> findArticle(translatedTitle, language, place));
        }
        return article.map(page -> new WikipediaResponse(page.title(), page.extract().trim(), page.fullurl()));
    }

    // Asks the Wikipedia of the language for the article with this title. The answer also has the article's title
    // in the other language (langlinks), which is used when the article was found in the other Wikipedia.
    private Optional<Page> findArticle(String title, String language, GeoJsonPoint place) {
        String linkLanguage = UserLanguage.ENGLISH.equals(language) ? UserLanguage.GERMAN : UserLanguage.ENGLISH;
        QueryResult result;
        try {
            result = wikipediaRestClient.get()
                    .uri(uri -> uri.path("/w/api.php")
                            .queryParam("action", "query")
                            .queryParam("format", "json")
                            .queryParam("formatversion", 2)
                            .queryParam("redirects", 1)
                            .queryParam("titles", title)
                            .queryParam("prop", "extracts|coordinates|pageprops|info|langlinks")
                            .queryParam("exintro", 1)
                            .queryParam("explaintext", 1)
                            .queryParam("exsentences", SENTENCES)
                            .queryParam("ppprop", "disambiguation")
                            .queryParam("inprop", "url")
                            .queryParam("lllang", linkLanguage)
                            // fills in {language} of the base URL
                            .build(language))
                    .header(HttpHeaders.USER_AGENT, USER_AGENT)
                    .retrieve()
                    .body(QueryResult.class);
        } catch (RestClientException e) {
            throw new LocalizedException(HttpStatus.SERVICE_UNAVAILABLE, "error.wikipedia.unavailable", e);
        }

        if (result == null || result.query() == null || result.query().pages() == null) {
            return Optional.empty();
        }
        return result.query().pages().stream()
                .filter(page -> isArticleNear(page, place))
                .findFirst();
    }

    // The title of the same article in another language, e.g. "Venice" for "Venedig" and "en"
    private static Optional<String> titleIn(Page page, String language) {
        if (page.langlinks() == null) {
            return Optional.empty();
        }
        return page.langlinks().stream()
                .filter(link -> language.equals(link.lang()) && link.title() != null && !link.title().isBlank())
                .map(LanguageLink::title)
                .findFirst();
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
                String fullurl,
                List<LanguageLink> langlinks) {
    }

    // The same article in another Wikipedia, e.g. {"lang": "en", "title": "Venice"}
    @JsonIgnoreProperties(ignoreUnknown = true)
    record LanguageLink(String lang, String title) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Coordinate(double lat, double lon) {
    }
}
