package org.example.backend.model;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "trips")
@With
public record Trip(@Id String id,
                   String title,
                   String description,
                   LocalDate startDate,
                   LocalDate endDate,
                   String coverColor,
                   List<TripTag> tags,
                   List<Stop> stops, // in the order of the visit, i.e. by date
                   String userId) {
}
