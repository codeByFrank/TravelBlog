# Architecture Decisions

Decisions that complement `requirements.md`. Update this file when a decision changes.

## Deployment

- The application is deployed on Render as one Docker container; the steps are in `deployment.md`.
- One container instead of two: the frontend build is copied into the backend's `static` folder, so the backend
  delivers the frontend and the API from the same address. This needs no CORS setup and no second service.
- The image is built by GitHub Actions (`deploy.yml`) on every push to `main` and stored on Docker Hub; Render only
  runs it. The image is built from the finished jar, so the Dockerfile stays short.
- Spring Security requires a login only for `/api/**`; all other paths are the frontend's files and are public.
- `FrontendController` forwards the React Router paths to `index.html`, so pages can be opened directly and reloaded.
- During local development, the Vite dev server proxies `/api` to the backend on `http://localhost:8080`.
- Continuous integration and code analysis: see CI and Code Quality.

## CI and Code Quality

- GitHub Actions runs three check workflows (`.github/workflows/`); the fourth, `deploy.yml`, deploys every push to
  `main` (see Deployment):
  - `maven.yml` (Java CI with Maven): builds and tests the backend with `mvn -B clean verify` on every push to a branch
    other than `main`.
  - `build_backend.yml` (SonarQube_backend): on every pull request and push to `main`, builds and tests the backend
    and analyzes it with SonarQube Cloud.
  - `build_frontend.yml` (SonarQube_frontend): on every pull request and push to `main`, sends `frontend/src` to
    SonarQube Cloud for analysis; it does not lint or build before the scan.
- SonarQube Cloud is used because it was introduced in the course; it reports bugs, code smells, security hotspots,
  duplication, and test coverage, and comments the Quality Gate result on each pull request.
- Backend and frontend are two separate SonarQube Cloud projects in the organization `dinara-mfl`:
  `dinara-mfl_TravelBlog_backend` and `dinara-mfl_TravelBlog_frontend`.
- The workflows authenticate with the repository secret `SONAR_TOKEN`; the token is never committed.
- A pull request is merged only when its Quality Gate has passed.
- Backend coverage is measured with JaCoCo (`jacoco-maven-plugin` in `backend/pom.xml`); `mvn verify` writes the report
  to `backend/target/site/jacoco/jacoco.xml`.
- `build_backend.yml` runs `mvn -B verify` and the SonarQube Maven plugin in one command, in the working directory
  `backend/`: Maven builds, runs the tests, writes the JaCoCo report, and then starts the analysis, so the analysis
  covers only the backend and includes coverage. The organization is set in `backend/pom.xml`
  (`sonar.organization`), the project key in the workflow.
- The backend is not analyzed with SonarQube's automatic configuration, because it does not fit a repository that
  contains backend and frontend.
- The frontend has no automated tests: frontend testing is not part of the course. Frontend coverage is therefore
  excluded from the analysis (`sonar.coverage.exclusions=**/*`). Lint, type check, and build are the frontend checks;
  they currently run only locally (`npm run lint`, `npm run build`), not in CI.

## Configuration & Secrets

- Secrets are provided via environment variables: `MONGODB_URI` (database connection string) and `JWT_SECRET` (key
  that signs login tokens, at least 32 characters). Setup is described in `README.md`.
- Tests use a fixed test-only secret from `backend/src/test/resources/application.properties`.
- `.env` files and keys are never committed.

## Authentication

- Users log in with username and password; they register themselves.
- Users are stored in the `users` collection; passwords are stored as hashes only.
- Spring Security protects every `/api` endpoint except `POST /api/auth/register` and `POST /api/auth/login`;
  without a valid token the backend answers 401.
- The login is kept with a JWT instead of a session cookie, because the backend then stays stateless
  (`SessionCreationPolicy.STATELESS`) and CSRF protection is not needed.
- `POST /api/auth/login` returns the token and the username. The token is signed with HMAC (`JWT_SECRET`), contains
  the user's id as subject, and is valid for 24 hours; there is no refresh token.
- The frontend stores the token in `localStorage` and sends it as `Authorization: Bearer <token>` with every request
  (`frontend/src/api/client.ts`). On a 401 it deletes the token and opens `/login`; logout deletes the token.
- `JwtFilter` reads the token; controllers get the logged-in user's id with `@AuthenticationPrincipal String userId`.
- All users have the same rights; there are no roles.
- Not supported: password reset by email.

## Data Model

- MongoDB with three collections: `users`, `trips`, `wishlist`.
- A trip document contains its stops as an embedded array, because a trip is always loaded together with its stops
  and the detail page then needs a single query.
- Every trip and wishlist entry stores the id of its owner (`userId`); every query filters by the logged-in user.
- A wishlist entry stores its countries as a list of ISO 3166-1 alpha-2 codes (`countryCodes`, 1 to 10). The user
  picks them from a fixed list in the frontend (`utils/countries.ts`, generated from the same Natural Earth data as the
  world map, with German names and continents), so no place search is needed for the wishlist. The form asks for the
  countries first; the place or region is optional. Without one, the frontend sends the country names as the
  destination (e.g. "Island", "Argentinien, Chile"), so the backend still always gets a destination and did not change.
  Entries stored before countries existed are returned with an empty list.
- The API returns DTOs (e.g. `TripResponse`, `WishlistEntryResponse`), not the documents: the owner id stays on the
  server.
- `GET /api/trips` returns the user's trips sorted by start date, newest first.
- MongoDB has no foreign keys: when a trip is deleted, its photos are deleted in code.
- Coordinates are stored as GeoJSON points (`{ type: "Point", coordinates: [longitude, latitude] }`). The API sends
  and receives plain `latitude` and `longitude` fields instead; the backend converts them in one place
  (`TripService` / `StopService`), so the frontend and Leaflet never deal with the GeoJSON order.

## Trips and Stops

- Every trip has at least one stop. `POST /api/trips` requires `firstStop` (place and stop type; its date is the
  start date of the trip), so that the trip and its first place are saved together, and the last stop cannot be
  deleted (400). Creating a trip for one place therefore takes one dialog. Trips saved before this rule may still
  have no stops and are shown as they are.
- The title is optional. An empty title is stored as `null`, and the backend builds it on every read from the
  stops (`TripTitles`): the distinct names of the overnight stops, otherwise of all stops, at most three
  ("Rom – Florenz – Bologna und 2 weitere"); "Neue Reise" for a trip without stops. Because it is built and not
  stored, it follows every change of the stops. `TripResponse.title` is always the title to show; `customTitle` is
  the user's own title or `null`, so that the edit form can leave the field empty.
- The order of the embedded `stops` array is the order of the visit. It follows the stop dates; stops with the same
  date keep the order in which they were added. There is no manual reordering.
- Each stop has a type (`StopType`: `OVERNIGHT`, `PASSING_THROUGH`, `DAY_TRIP`), chosen by the user in the stop
  dialog and for the first stop in the trip dialog (preselected: overnight for a trip of several days, otherwise day
  trip). It is set by the user and not derived from the dates, because a stop without an overnight stay on the day of
  a move can be a day trip as well as a stop on the way. It replaced the boolean `overnight` (2026-11-20). Stops saved
  before keep their `overnight` field in MongoDB and are read as `OVERNIGHT` (true) or `DAY_TRIP` (false or missing)
  through `Stop.typeOrDefault()`; there is no migration script. New stops store no `overnight` field.
- The route (`TripRoute`) is made of the overnight stops and the stops passed on the way, in the order of the visit;
  a day trip belongs to the last overnight stop before it (its base) and is not part of the route. A trip without
  any overnight stop has no base, so all its stops are the route. `StopResponse` tells the frontend what to draw:
  `onRoute`, `baseStopId` (for a day trip), `distanceFromPreviousKm` (from the previous stop on the route, for a day
  trip from its base; null if there is none), and `departureDate` (see below).
- `departureDate` is the last day of a stay: for an overnight stop the arrival at the next overnight stop or the end of
  the trip, for the only stop of a trip the end of the trip, otherwise the arrival date. The backend computes it, so
  the frontend only formats it ("12.–15. Mai · 3 Nächte").
- A stop has one date, the arrival date. It must lie within the dates of the trip; a trip's dates cannot be changed
  so that a stop would lie outside them (both answered with 400).
- Each stop has an id (UUID) generated by the backend, so that a single stop can be changed or deleted:
  `POST /api/trips/{tripId}/stops`, `PUT` and `DELETE /api/trips/{tripId}/stops/{stopId}`. Each of them returns the
  whole trip, because the order of the stops and the distances can change.
- On the trip detail page, `TripStops` shows the stops in order with the distances between them; `StopDialog` adds
  and changes a stop. For now a stop is added by searching its place (`PlaceSearch`); adding by a click on the map
  follows with the trip map.
- `TripResponse` contains the stops with `distanceFromPreviousKm` and the trip's `routeLengthKm`; the frontend does
  not calculate distances.
- Each stop stores an ISO 3166-1 alpha-2 country code, set when the stop is created (from the place search or
  chosen by the user), because the world map and the statistics then need no geocoding.
- The route length is calculated in the backend with the haversine formula as the sum of the straight-line distances
  between consecutive stops on the route; day trips do not count. Routes along roads are not supported. A trip with
  one stop on the route has a length of 0, so the detail page and the trip card show the number of stops from two
  stops on and the route length only when it is not 0.

## Maps

- Maps use Leaflet with OpenStreetMap tiles, because both are free and need no API key.
- The world map colors countries from a GeoJSON file of country borders, matched by country code.
- The country borders are `frontend/public/countries.geojson`: a smaller copy of the Natural Earth countries
  (1:110m, public domain) with only the ISO code (`ISO_A2_EH`, because `ISO_A2` is `-99` for e.g. France and Norway),
  the German name, and the continent; Antarctica is left out. The file is loaded when the world map opens.
- French Guiana is a separate area with the code `GF` in that file, although Natural Earth counts it as part of France.
  Otherwise a wish or visit "Frankreich" would also color a piece of South America, which confuses users.
- The world map has no tile layer: it shows only the countries, colored as in the mockup (visited, wishlist, other).
- Leaflet is used through `react-leaflet`, so maps are written as React components.
- The trip map (`TripMap`) is part of "Reiseverlauf" on the detail page, as in the mockup: the map card on the left,
  the stops on the right; on narrow screens the map is above the list. On wide screens the card is sticky, so the map
  stays in view while the user scrolls through the stops. It shows OpenStreetMap tiles, the stops as numbered markers
  with their names (blue on the route, white with a blue ring for day trips), a dashed line between the stops on the
  route in the order of the visit, a thinner dotted line from its base to each day trip (not counted in the route
  length; day trips without a base have none), and below the map the number of places and the route length
  (`routeLengthKm`, shown only when there is a route).
  A click on a marker opens a popup with the name, dates, country, and note. In the list, a day trip shows its base
  and distance in its badge ("Tagesausflug ab Rom · 26 km") and gets no distance arrow; the arrow before the next stop
  on the route names where it is measured from when day trips lie in between ("186 km ab Rom").
  The markers are `divIcon`s styled with Tailwind, because Leaflet's default marker images are not found after the
  Vite build. A trip without stops shows no map.
- A single place gets a smaller map, zoomed out (at most zoom 5), so that it shows where in the country or the
  region the place lies; with a route the map is higher and zooms in up to zoom 12.
- The trip map fits itself to the stops when it opens and again when a stop is added, moved, or deleted, but not when
  only a name or a note changes, so it stays where the user moved it.
- On both maps a pinch on the touchpad or Ctrl (⌘) + mouse wheel zooms the map; plain scrolling scrolls the page and
  shows a short hint (`MapWheelZoom`). Browsers report a pinch as a wheel event with `ctrlKey`, so Leaflet's wheel zoom
  stays on and the component only keeps plain scrolling away from it. (Before, the wheel zoomed only after a click into
  the map, and a pinch zoomed the whole page.)
- The list of visited countries is the set of distinct country codes over all stops of the user. The frontend
  collects them from `GET /api/trips` (`utils/visitedCountries.ts`); there is no separate endpoint.
- Countries too small for the 1:110m borders (e.g. Malta) still count as visited and appear in the list, with the
  German name from the browser (`Intl.DisplayNames`), but they are not colored on the map and have no continent.
- Clicking a visited country shows its trips below the map; clicking a wishlist country shows its wishlist
  destinations there (both parts for a country that is both). The entries of the list "Besuchte Länder" are buttons that
  do the same, because the countries on the Leaflet map cannot be reached with the keyboard.

## Place Search

- The place search uses Nominatim (OpenStreetMap). It returns the coordinates and the country code for a stop.
- Nominatim allows at most one request per second; the search is sent when the user submits it, not on every
  keystroke.
- The frontend does not call Nominatim directly but `GET /api/places?query=…`; the backend (`PlaceService`) asks
  Nominatim with a User-Agent that names the application, as its usage policy requires, and returns up to five
  places with name, full name, coordinates, and the country code in upper case. Results without a country (e.g. seas)
  are left out. If Nominatim cannot be reached, the API answers 503 with a German message.
- The Nominatim address is configured in `app.nominatim.base-url`; the tests use a fake address and
  `MockRestServiceServer`, so they never call the real service.

## Photos

- A stop stores references to its photos; the photo files are not stored inside the trip document.
- Not decided: where the files are stored and which limits apply (see `requirements.md` → Open Questions).

## Tags

- The tags are a fixed list: `MOUNTAINS` (Berge), `SEA` (Meer), `CITY_TRIP` (Städtetrip), `FOOD` (Essen),
  `FRIENDS_AND_FAMILY` (Freunde & Familie) — the enum `TripTag` in the backend, the German labels in
  `frontend/src/types/trip.ts`. A fixed list avoids typos and duplicates such as "Meer" and "meer", and the filter
  always shows the same buttons.
- A trip stores its tags as a list in the trip document. The backend removes duplicates and sorts them in enum order;
  trips saved before tags existed are returned with an empty list. An unknown tag is rejected with 400.
- Stops will use the same list once they exist; a trip then matches a tag if the trip or one of its stops has it.

## Search and Statistics

- Search and the year filter on the trip overview run in the frontend (`frontend/src/utils/tripFilters.ts`): the page
  already loads all trips of the user, one person has tens of trips rather than thousands, and the result appears
  without a new request.
- A trip belongs to the year in which it starts.
- The tag filter accepts several tags and combines them with AND: only trips that have every selected tag are
  shown. "Alle" clears the selected tags. Tag, year, and search filters apply together.
- Free-text search covers trip titles, descriptions, tag names, and the stops' names, countries (German names), and
  notes, so that "Florenz", "Frankreich" or "Eis" find a trip.
- Statistics are calculated from the user's trips on request; nothing is stored redundantly. The overview totals
  (`getTotals`: travel days, distinct countries, stops, sum of the route lengths) are computed in the frontend from
  the trips it already loaded; they ignore search and filters. A trip card shows its countries in route order and,
  from two stops on, the number of stops and the route length.

## Frontend Conventions

- Styling uses Tailwind CSS v4. Theme colors are defined once in `frontend/src/index.css`; components use these
  colors instead of hard-coded ones.
- The look follows the mockups in `TravelBlog – Seitenentwürfe.html`: light background, white cards with soft
  shadows and large corner radii, one blue accent color.
- Small screens are covered with Tailwind breakpoints; the main breakpoint is `md` (768 px).
- Pages load data once and are refreshed manually (no polling).
- Routing uses React Router; HTTP requests use axios.
- Code layout in `frontend/src/`: `pages/` (one component per route, named `…Page`), `components/` (reusable parts),
  `api/` (one file per backend resource, built on the shared axios client in `api/client.ts`), `types/` (types of the
  API responses), `utils/` (helpers such as date formatting).
- The menu pages are defined once in `navigation.tsx`; the routes in `App.tsx` and the links in `NavBar.tsx` are built
  from it. Pages behind the login are wrapped in `ProtectedRoute`.
- Dates come from the API as ISO strings (`2024-05-12`) and are shown in German with `Intl.DateTimeFormat`
  (`utils/dates.ts`), e.g. "12.–20. Mai 2024"; they are formatted in UTC so the day does not shift with the time zone.
- Until trips have cover images, the trip cards use the four pastel cover colors of the mockups (`cover-1` to
  `cover-4` in the theme).
- User interface texts are in German. A language switch is a future idea and not built yet; the application name
  TravelBlog is the same in every language.

## Error Handling

- API errors use RFC 9457 `ProblemDetail` (`spring.mvc.problemdetails.enabled=true`); the frontend shows its `detail`
  text (`getErrorMessage` in `api/client.ts`) or a German fallback message.
- Validation errors include an `errors` object (field name → message).
- A request for a resource of another user is answered like a missing resource (404).

## Testing

- Controllers are tested as integration tests: `@SpringBootTest` with MockMvc against the full application context
  and an embedded MongoDB (Flapdoodle), because this is the approach taught in the course and it also covers the
  repository and the JSON mapping.
- The embedded MongoDB is started by the tests themselves; no installed database or other infrastructure is needed.
  The first run downloads the MongoDB binary.
- Each test starts with an empty database.
- Service logic (e.g. the route length) is tested with plain unit tests; the repository is mocked with Mockito.
- Calls to external services (e.g. Nominatim) are mocked with `MockRestServiceServer`.
