package org.example.backend.model;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;

/**
 * A place visited during a trip, stored inside the trip document.
 *
 * @param id          generated when the stop is added, so that it can be changed or deleted later
 * @param name        e.g. "Florenz"
 * @param location    GeoJSON point: x is the longitude, y the latitude
 * @param countryCode ISO 3166-1 alpha-2 code, e.g. "IT", used by the world map and the statistics
 * @param date        arrival date, within the dates of the trip
 * @param note        personal note; optional
 * @param type        how the user spent the time here; null for stops saved before this field existed
 * @param overnight   only in stops saved before {@code type} existed: true if the user stayed overnight.
 *                    New stops store null here, so MongoDB leaves the field out. Read {@link #typeOrDefault()}.
 */
public record Stop(String id,
                   String name,
                   GeoJsonPoint location,
                   String countryCode,
                   LocalDate date,
                   String note,
                   StopType type,
                   Boolean overnight) {

    // The type, also for stops saved before it existed: an overnight stay stays one, anything else was a day trip
    public StopType typeOrDefault() {
        if (type != null) {
            return type;
        }
        return Boolean.TRUE.equals(overnight) ? StopType.OVERNIGHT : StopType.DAY_TRIP;
    }
}
