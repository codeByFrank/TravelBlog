package org.example.backend.controller;

import org.example.backend.dto.WikipediaResponse;
import org.example.backend.service.UserLanguage;
import org.example.backend.service.WikipediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

/**
 * The beginning of the Wikipedia article about a stop's place,
 * e.g. GET /api/wikipedia?name=Florenz&latitude=43.77&longitude=11.26
 */
@RestController
@RequestMapping("/api/wikipedia")
public class WikipediaController {

    private final WikipediaService wikipediaService;

    public WikipediaController(WikipediaService wikipediaService) {
        this.wikipediaService = wikipediaService;
    }

    // 204 No Content if there is no article about this place, so the frontend simply shows nothing
    @GetMapping
    public ResponseEntity<WikipediaResponse> getArticle(@RequestParam String name,
                                                        @RequestParam double latitude,
                                                        @RequestParam double longitude,
                                                        Locale locale) {
        // The article in the language of the user interface (Accept-Language header)
        return wikipediaService.find(name, latitude, longitude, UserLanguage.of(locale))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
