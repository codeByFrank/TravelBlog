package org.example.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * The data the frontend sends when a stop is added or changed.
 * Name, coordinates and country code usually come from the place search.
 */
public record StopRequest(
        @NotBlank(message = "Bitte einen Namen eingeben.")
        @Size(max = 100, message = "Der Name darf höchstens 100 Zeichen lang sein.")
        String name,

        @NotNull(message = "Bitte einen Ort auswählen.")
        @DecimalMin(value = "-90", message = "Ungültige Koordinaten.")
        @DecimalMax(value = "90", message = "Ungültige Koordinaten.")
        Double latitude,

        @NotNull(message = "Bitte einen Ort auswählen.")
        @DecimalMin(value = "-180", message = "Ungültige Koordinaten.")
        @DecimalMax(value = "180", message = "Ungültige Koordinaten.")
        Double longitude,

        @NotNull(message = "Bitte ein Land angeben.")
        @Pattern(regexp = "^[A-Z]{2}$", message = "Ungültiger Ländercode.")
        String countryCode,

        @NotNull(message = "Bitte ein Datum wählen.")
        LocalDate date,

        @Size(max = 2000, message = "Die Notiz darf höchstens 2000 Zeichen lang sein.")
        String note) {
}
