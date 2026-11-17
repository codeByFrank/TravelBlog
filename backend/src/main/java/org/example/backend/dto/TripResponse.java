package org.example.backend.dto;

import org.example.backend.model.TripTag;

import java.time.LocalDate;
import java.util.List;

public record TripResponse(String id,
                           String title,
                           String description,
                           LocalDate startDate,
                           LocalDate endDate,
                           String coverColor,
                           List<TripTag> tags) {
}
