package org.example.backend.model;

/**
 * How the user spent the time at a stop. It decides whether the stop is part of the route of the trip.
 */
public enum StopType {
    // Stayed overnight here; the overnight stops are the route of the trip
    OVERNIGHT,
    // Stopped on the way to the next overnight stop; part of the route
    PASSING_THROUGH,
    // Went there and back from the last overnight stop on the same day; not part of the route
    DAY_TRIP
}
