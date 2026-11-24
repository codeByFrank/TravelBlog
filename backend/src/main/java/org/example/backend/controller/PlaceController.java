package org.example.backend.controller;

import org.example.backend.dto.PlaceResponse;
import org.example.backend.service.PlaceService;
import org.example.backend.service.UserLanguage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

/**
 * Place search for new stops, e.g. GET /api/places?query=Florenz
 */
@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public List<PlaceResponse> searchPlaces(@RequestParam(defaultValue = "") String query, Locale locale) {
        // Place and country names in the language of the user interface (Accept-Language header)
        return placeService.search(query, UserLanguage.of(locale));
    }
}
