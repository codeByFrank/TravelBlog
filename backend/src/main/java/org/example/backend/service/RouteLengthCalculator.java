package org.example.backend.service;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

/**
 * Straight-line distances between places with the haversine formula.
 * Routes along roads are not supported (see architecture.md → Trips and Stops).
 */
public final class RouteLengthCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private RouteLengthCalculator() {
    }

    // Distance in km between two GeoJSON points (x = longitude, y = latitude)
    public static double distanceKm(GeoJsonPoint from, GeoJsonPoint to) {
        double fromLat = Math.toRadians(from.getY());
        double toLat = Math.toRadians(to.getY());
        double deltaLat = toLat - fromLat;
        double deltaLon = Math.toRadians(to.getX() - from.getX());

        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(fromLat) * Math.cos(toLat) * Math.pow(Math.sin(deltaLon / 2), 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(a));
    }

    // Sum of the distances between consecutive points, rounded to whole km; 0 for fewer than two points
    public static long totalKm(List<GeoJsonPoint> points) {
        double total = 0;
        for (int i = 1; i < points.size(); i++) {
            total += distanceKm(points.get(i - 1), points.get(i));
        }
        return Math.round(total);
    }
}
