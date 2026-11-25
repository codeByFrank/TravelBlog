package org.example.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.example.backend.model.TripTag;

import java.time.LocalDate;
import java.util.List;

public record TripRequest(
        // Optional; without a title the trip is named after its places (see TripTitles)
        @Size(max = 100, message = "{validation.title.size}")
        String title,

        @Size(max = 2000, message = "{validation.description.size}")
        String description,

        @NotNull(message = "{validation.startDate.required}")
        LocalDate startDate,

        @NotNull(message = "{validation.endDate.required}")
        LocalDate endDate,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "{validation.color.invalid}")
        String coverColor,

        // Optional; unknown values are rejected when the JSON is read
        List<TripTag> tags,

        // Required when a trip is created, ignored when it is changed: every trip has at least one place
        @Valid
        FirstStopRequest firstStop) {
}