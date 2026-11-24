package org.example.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.backend.model.StopType;

/**
 * The first place of a new trip, sent together with the trip.
 * Like {@link StopRequest}, but without date and note: the arrival date is the start date of the trip.
 */
public record FirstStopRequest(
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

        @NotNull(message = "{validation.stopType.required}")
        StopType type) {
}
