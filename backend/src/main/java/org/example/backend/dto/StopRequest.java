package org.example.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.backend.model.StopType;

import java.time.LocalDate;

/**
 * The data the frontend sends when a stop is added or changed.
 * Name, coordinates and country code usually come from the place search.
 */
public record StopRequest(
        @NotBlank(message = "{validation.name.required}")
        @Size(max = 100, message = "{validation.name.size}")
        String name,

        @NotNull(message = "{validation.place.required}")
        @DecimalMin(value = "-90", message = "{validation.coordinates.invalid}")
        @DecimalMax(value = "90", message = "{validation.coordinates.invalid}")
        Double latitude,

        @NotNull(message = "{validation.place.required}")
        @DecimalMin(value = "-180", message = "{validation.coordinates.invalid}")
        @DecimalMax(value = "180", message = "{validation.coordinates.invalid}")
        Double longitude,

        @NotNull(message = "{validation.country.required}")
        @Pattern(regexp = "^[A-Z]{2}$", message = "{validation.countryCode.invalid}")
        String countryCode,

        @NotNull(message = "{validation.date.required}")
        LocalDate date,

        @Size(max = 2000, message = "{validation.note.size}")
        String note,

        @NotNull(message = "{validation.stopType.required}")
        StopType type) {
}
