package org.example.backend.service;

import org.example.backend.model.Priority;
import org.example.backend.model.Stop;
import org.example.backend.model.StopType;
import org.example.backend.model.Trip;
import org.example.backend.model.TripTag;
import org.example.backend.model.WishlistEntry;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The sample trips, photos and wishlist entries of the demo user (see {@link DemoDataSeeder}).
 * The trips are in different years and countries, so that the world map, the statistics and the filters show
 * something. The texts are in German like the rest of the user interface.
 */
final class DemoData {

    // Folder of the sample photos in src/main/resources; free photos from Unsplash (Unsplash License)
    static final String PHOTO_FOLDER = "demo-photos/";

    // Which stop gets which sample photo: stop name → file in PHOTO_FOLDER
    static final Map<String, String> PHOTO_OF_STOP = Map.ofEntries(
            Map.entry("Florenz", "florenz.jpg"),
            Map.entry("Venedig", "venedig.jpg"),
            Map.entry("Rom", "rom.jpg"),
            Map.entry("Lissabon", "lissabon.jpg"),
            Map.entry("Porto", "porto.jpg"),
            Map.entry("Reykjavík", "reykjavik.jpg"),
            Map.entry("Seljalandsfoss", "seljalandsfoss.jpg"),
            Map.entry("Jökulsárlón", "jokulsarlon.jpg"),
            Map.entry("Tokio", "tokio.jpg"),
            Map.entry("Hakone", "hakone.jpg"),
            Map.entry("Kyoto", "kyoto.jpg"));

    // The photo of these stops is the cover photo of their trip (one per trip)
    static final Set<String> COVER_STOPS = Set.of("Venedig", "Lissabon", "Seljalandsfoss", "Kyoto");

    private DemoData() {
        // only static methods
    }

    static List<Trip> trips(String userId) {
        return List.of(
                italy(userId),
                portugal(userId),
                iceland(userId),
                japan(userId));
    }

    static List<WishlistEntry> wishlist(String userId) {
        return List.of(
                new WishlistEntry(null, userId,
                        "Patagonien",
                        "Gletscher sehen und im Torres del Paine wandern",
                        Priority.HIGH,
                        "Dezember bis März",
                        List.of("AR", "CL")),
                new WishlistEntry(null, userId,
                        "Neuseeland", // only the country name: the wishlist shows it without a place
                        "Fjorde, Vulkane und die Drehorte von Herr der Ringe",
                        Priority.HIGH,
                        "Januar bis März",
                        List.of("NZ")),
                new WishlistEntry(null, userId,
                        "Lofoten",
                        "Nordlichter sehen",
                        Priority.MEDIUM,
                        "Februar",
                        List.of("NO")),
                new WishlistEntry(null, userId,
                        "Rocky Mountains",
                        "Mit dem Camper durch Banff und Jasper fahren",
                        Priority.MEDIUM,
                        "Juli bis September",
                        List.of("CA")),
                new WishlistEntry(null, userId,
                        "Machu Picchu",
                        "Den Inka-Pfad laufen",
                        Priority.LOW,
                        null,
                        List.of("PE")));
    }

    private static Trip italy(String userId) {
        List<Stop> stops = List.of(
                stop("Florenz", 43.7696, 11.2558, "IT", LocalDate.of(2024, 5, 10), StopType.OVERNIGHT,
                        "Sonnenuntergang am Piazzale Michelangelo"),
                stop("Siena", 43.3188, 11.3308, "IT", LocalDate.of(2024, 5, 12), StopType.DAY_TRIP,
                        "Piazza del Campo und der Dom"),
                stop("Bologna", 44.4949, 11.3426, "IT", LocalDate.of(2024, 5, 13), StopType.PASSING_THROUGH,
                        "Mittagessen: Tagliatelle al ragù"),
                stop("Venedig", 45.4408, 12.3155, "IT", LocalDate.of(2024, 5, 13), StopType.OVERNIGHT,
                        "Früh morgens am Markusplatz – fast menschenleer"),
                stop("Rom", 41.9028, 12.4964, "IT", LocalDate.of(2024, 5, 16), StopType.OVERNIGHT,
                        "Kolosseum, Trastevere und jeden Tag ein Gelato"));
        return new Trip(null,
                "Toskana, Venedig und Rom",
                "Zehn Tage Italien mit dem Zug: Kunst, Kanäle und sehr viel gutes Essen.",
                LocalDate.of(2024, 5, 10),
                LocalDate.of(2024, 5, 20),
                "#F6EADF", // Pfirsich
                List.of(TripTag.CITY_TRIP, TripTag.FOOD),
                stops,
                userId,
                null);
    }

    private static Trip portugal(String userId) {
        List<Stop> stops = List.of(
                stop("Lissabon", 38.7223, -9.1393, "PT", LocalDate.of(2025, 4, 3), StopType.OVERNIGHT,
                        "Mit der Tram 28 durch die Alfama"),
                stop("Sintra", 38.8029, -9.3817, "PT", LocalDate.of(2025, 4, 5), StopType.DAY_TRIP,
                        "Der Palácio da Pena im Nebel"),
                stop("Coimbra", 40.2033, -8.4103, "PT", LocalDate.of(2025, 4, 7), StopType.PASSING_THROUGH,
                        "Die alte Universität und ihre Bibliothek"),
                stop("Porto", 41.1579, -8.6291, "PT", LocalDate.of(2025, 4, 7), StopType.OVERNIGHT,
                        "Portweinprobe in Vila Nova de Gaia"));
        return new Trip(null,
                null, // no own title: the trip is named after its stops
                "Osterferien mit der Familie an der Atlantikküste.",
                LocalDate.of(2025, 4, 3),
                LocalDate.of(2025, 4, 10),
                "#DCEAF7", // Himmelblau
                List.of(TripTag.CITY_TRIP, TripTag.SEA, TripTag.FRIENDS_AND_FAMILY),
                stops,
                userId,
                null);
    }

    private static Trip iceland(String userId) {
        List<Stop> stops = List.of(
                stop("Reykjavík", 64.1466, -21.9426, "IS", LocalDate.of(2023, 8, 14), StopType.OVERNIGHT,
                        "Start der Rundreise mit dem Mietwagen"),
                stop("Seljalandsfoss", 63.6156, -19.9886, "IS", LocalDate.of(2023, 8, 16), StopType.PASSING_THROUGH,
                        "Einmal hinter dem Wasserfall entlang"),
                stop("Vík", 63.4186, -19.0060, "IS", LocalDate.of(2023, 8, 16), StopType.OVERNIGHT,
                        "Der schwarze Strand Reynisfjara"),
                stop("Jökulsárlón", 64.0784, -16.2306, "IS", LocalDate.of(2023, 8, 18), StopType.PASSING_THROUGH,
                        "Eisberge in der Gletscherlagune"),
                stop("Akureyri", 65.6885, -18.1262, "IS", LocalDate.of(2023, 8, 19), StopType.OVERNIGHT,
                        "Walbeobachtung im Eyjafjörður"),
                stop("Mývatn", 65.6039, -16.9961, "IS", LocalDate.of(2023, 8, 21), StopType.DAY_TRIP,
                        "Baden in den Mývatn Nature Baths"));
        return new Trip(null,
                "Island – Ringstraße",
                "Mit dem Auto einmal um die Insel: Wasserfälle, Gletscher und Wale.",
                LocalDate.of(2023, 8, 14),
                LocalDate.of(2023, 8, 24),
                "#E0EFEA", // Mint
                List.of(TripTag.MOUNTAINS, TripTag.SEA),
                stops,
                userId,
                null);
    }

    private static Trip japan(String userId) {
        List<Stop> stops = List.of(
                stop("Tokio", 35.6762, 139.6503, "JP", LocalDate.of(2022, 10, 20), StopType.OVERNIGHT,
                        "Die Shibuya-Kreuzung bei Nacht"),
                stop("Hakone", 35.2324, 139.1069, "JP", LocalDate.of(2022, 10, 24), StopType.OVERNIGHT,
                        "Ryokan mit Onsen und Blick auf den Fuji"),
                stop("Kyoto", 35.0116, 135.7681, "JP", LocalDate.of(2022, 10, 26), StopType.OVERNIGHT,
                        "Fushimi Inari früh am Morgen"),
                stop("Nara", 34.6851, 135.8048, "JP", LocalDate.of(2022, 10, 29), StopType.DAY_TRIP,
                        "Die zahmen Rehe im Nara-Park"),
                stop("Osaka", 34.6937, 135.5023, "JP", LocalDate.of(2022, 10, 30), StopType.OVERNIGHT,
                        "Streetfood in Dōtonbori"));
        return new Trip(null,
                "Japan im Herbst",
                "Zwei Wochen mit dem Japan Rail Pass von Tokio bis Osaka.",
                LocalDate.of(2022, 10, 20),
                LocalDate.of(2022, 11, 2),
                "#EAE6F5", // Lavendel
                List.of(TripTag.CITY_TRIP, TripTag.FOOD),
                stops,
                userId,
                null);
    }

    // One stop of a trip; GeoJsonPoint takes the longitude first, then the latitude
    private static Stop stop(String name, double latitude, double longitude, String countryCode,
                             LocalDate date, StopType type, String note) {
        return new Stop(UUID.randomUUID().toString(),
                name,
                new GeoJsonPoint(longitude, latitude),
                countryCode,
                date,
                note,
                type,
                null,
                List.of());
    }
}
