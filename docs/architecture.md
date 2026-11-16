# Architecture Decisions

Decisions that complement `requirements.md`. Update this file when a decision changes.

## Deployment

- The deployment target is not decided (see `requirements.md` → Open Questions).
- During local development, the Vite dev server proxies `/api` to the backend on `http://localhost:8080`.
- Continuous integration and code analysis: see CI and Code Quality.

## CI and Code Quality

- GitHub Actions runs three workflows (`.github/workflows/`):
  - `maven.yml` (Java CI with Maven): builds and tests the backend with `mvn -B clean verify` on every push to a branch
    other than `main`.
  - `build_backend.yml` (SonarQube_backend) and `build_frontend.yml` (SonarQube_frontend): on every pull request and
    push to `main`, send the code to SonarQube Cloud for analysis. They do not build, test, or lint before the scan.
- SonarQube Cloud is used because it was introduced in the course; it reports bugs, code smells, security hotspots,
  duplication, and test coverage, and comments the Quality Gate result on each pull request.
- Backend and frontend are two separate SonarQube Cloud projects in the organization `dinara-mfl`:
  `dinara-mfl_TravelBlog_backend` and `dinara-mfl_TravelBlog_frontend`.
- The workflows authenticate with the repository secret `SONAR_TOKEN`; the token is never committed.
- A pull request is merged only when its Quality Gate has passed.
- Backend coverage is measured with JaCoCo (`jacoco-maven-plugin` in `backend/pom.xml`); `mvn verify` writes the report
  to `backend/target/site/jacoco/jacoco.xml`.
- Not set up at the moment: `build_backend.yml` was reset to the plain SonarQube template on 2026-11-16, so it neither
  builds nor tests before the scan, does not pass the JaCoCo report, and does not limit the analysis to `backend/`.
  Backend coverage therefore does not reach SonarQube Cloud, and frontend files can show up in the backend analysis.
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
- The API returns DTOs (e.g. `TripResponse`), not the documents: the owner id stays on the server.
- `GET /api/trips` returns the user's trips sorted by start date, newest first.
- MongoDB has no foreign keys: when a trip is deleted, its photos are deleted in code.
- Coordinates are stored as GeoJSON points (`{ type: "Point", coordinates: [longitude, latitude] }`). Leaflet
  expects `[latitude, longitude]`; the order is converted in one place in the frontend.

## Trips and Stops

- The order of the embedded `stops` array is the order of the visit.
- Each stop stores an ISO 3166-1 alpha-2 country code, set when the stop is created (from the place search or
  chosen by the user), because the world map and the statistics then need no geocoding.
- The route length is calculated in the backend with the haversine formula as the sum of the straight-line distances
  between consecutive stops. Routes along roads are not supported.

## Maps

- Maps use Leaflet with OpenStreetMap tiles, because both are free and need no API key.
- The world map colors countries from a GeoJSON file of country borders, matched by country code.
- The list of visited countries is the set of distinct country codes over all stops of the user.

## Place Search

- The place search uses Nominatim (OpenStreetMap). It returns the coordinates and the country code for a stop.
- Nominatim allows at most one request per second; the search is sent when the user submits it, not on every
  keystroke.

## Photos

- A stop stores references to its photos; the photo files are not stored inside the trip document.
- Not decided: where the files are stored and which limits apply (see `requirements.md` → Open Questions).

## Search and Statistics

- Filtering by year and country uses queries on the trip's dates and on `stops.countryCode`.
- Free-text search covers trip titles, stop names, and notes.
- Statistics are calculated from the user's trips on request; nothing is stored redundantly.

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
