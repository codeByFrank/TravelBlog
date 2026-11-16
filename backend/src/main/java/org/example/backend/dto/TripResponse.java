package org.example.backend.dto;

import java.time.LocalDate;

public record TripResponse(String id,
                           String title,
                           String description,
                           LocalDate startDate,
                           LocalDate endDate) {
}
