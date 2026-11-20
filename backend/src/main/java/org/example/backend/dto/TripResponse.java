package org.example.backend.dto;

import org.example.backend.model.TripTag;

import java.time.LocalDate;
import java.util.List;

public record TripResponse(String id,
                           String title, // the title of the user or, without one, built from the stops
                           String customTitle, // the title of the user; null if the title is built from the stops
                           String description,
                           LocalDate startDate,
                           LocalDate endDate,
                           String coverColor,
                           List<TripTag> tags,
                           List<StopResponse> stops,
                           long routeLengthKm,
                           String coverPhotoId) {
}
