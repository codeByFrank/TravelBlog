package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TripRequest(
        @NotBlank(message = "Bitte einen Titel eingeben.")
        @Size(max = 100, message = "Der Titel darf höchstens 100 Zeichen lang sein.")
        String title,

        @Size(max = 2000, message = "Die Beschreibung darf höchstens 2000 Zeichen lang sein.")
        String description,

        @NotNull(message = "Bitte ein Startdatum wählen.")
        LocalDate startDate,

        @NotNull(message = "Bitte ein Enddatum wählen.")
        LocalDate endDate,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Ungültige Farbe.")
        String coverColor) {
}