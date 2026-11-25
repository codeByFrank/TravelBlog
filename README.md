# TravelBlog

**A personal travel diary on a map.** Record your trips stop by stop, add photos and notes, see your routes and every
country you have visited, and keep a wishlist of places you still want to see.

[![Prod deploy](https://github.com/dinara-mfl/TravelBlog/actions/workflows/deploy.yml/badge.svg)](https://github.com/dinara-mfl/TravelBlog/actions/workflows/deploy.yml)
[![Quality Gate backend](https://sonarcloud.io/api/project_badges/measure?project=dinara-mfl_TravelBlog_backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=dinara-mfl_TravelBlog_backend)
[![Quality Gate frontend](https://sonarcloud.io/api/project_badges/measure?project=dinara-mfl_TravelBlog_frontend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=dinara-mfl_TravelBlog_frontend)
![Java 25](https://img.shields.io/badge/Java-25-007396?logo=openjdk&logoColor=white)
![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot&logoColor=white)
![React 19](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?logo=mongodb&logoColor=white)

TravelBlog is a full-stack group project of three developers, **built in 10 working days** in a Java + React
bootcamp: a Spring Boot REST API with MongoDB, a React single-page app with interactive maps, and a CI/CD pipeline that
tests, analyzes, and deploys every change.

![Trip overview in the light and the dark design](docs/screenshots/light-dark.png)

## Try it live

**[travelblog-latest.onrender.com](https://travelblog-latest.onrender.com/)**. Log in with the demo account, no
registration needed:

| Username | Password   |
|----------|------------|
| `demo`   | `12345678` |

The demo account has four sample trips (Italy, Portugal, Iceland, Japan) with photos, and a wishlist. Feel free to
change or delete anything: the demo data is reset whenever the service restarts.

**API documentation:** [Swagger UI](https://travelblog-latest.onrender.com/swagger-ui.html) lists every endpoint and
lets you try it in the browser: log in with `POST /api/auth/login`, copy the token, click **Authorize**, and paste it.

> The app runs on Render's free plan, which sleeps after a while without visitors. The first page load can take about a
> minute; after that it is fast. If the server falls asleep while the app is open, the app shows a note while it wakes
> up.

The user interface is in German and English, in a light and a dark design: switch with **DE | EN** and the sun / moon
button in the top right corner.

## Highlights

- **10 working days, 3 developers, 45+ reviewed pull requests:** feature branches, code review by a teammate, and a
  protected `main` branch
- **Continuous deployment:** every merge is tested, packaged as a Docker image, and live on Render within minutes
- **150+ automated backend tests** with MockMvc and an embedded MongoDB; coverage measured with JaCoCo
- **SonarQube Quality Gate on every pull request**, for the backend and the frontend
- **Lighthouse 100 / 100 / 100 / 100** in performance, accessibility, best practices, and SEO, on mobile and desktop
- **20 REST endpoints**, secured with Spring Security and JWT and documented with OpenAPI / Swagger UI; every user
  sees and changes only their own data
- **Interactive maps** with Leaflet: the route of each trip and a world map of all visited and wished-for countries
- **Three external services** integrated without API keys: OpenStreetMap, Nominatim place search, and Wikipedia
- **Two languages, two designs:** German and English (also error messages, place search, and Wikipedia), light and
  dark mode
- **Polished details:** photo upload with gallery and cover photo, skeleton loading, a note while the server wakes up,
  link previews for LinkedIn and others, a demo account with sample data

## Screenshots

The screenshots show the English interface of the demo account.

| Trip overview | Trip with route map |
|---|---|
| ![Trip overview with photos, filters, and tags](docs/screenshots/trips.png) | ![Trip detail with the route on a map](docs/screenshots/trip-detail.png) |
| **Stops with photos and Wikipedia** | **World map: visited countries and wishes** |
| ![Stops with photos and a Wikipedia summary](docs/screenshots/trip-photos.png) | ![World map with visited and wished-for countries](docs/screenshots/world-map.png) |
| **Statistics** | **Wishlist** |
| ![Statistics with records and years](docs/screenshots/statistics.png) | ![Wishlist with priorities and "plan a trip"](docs/screenshots/wishlist.png) |
| **Dark mode** | **API documentation (Swagger UI)** |
| ![Trip with route map in the dark design](docs/screenshots/dark-mode.png) | ![Swagger UI with all REST endpoints](docs/screenshots/swagger.png) |
| **Note while the server wakes up** | |
| ![A note at the bottom of the login page while the server wakes up](docs/screenshots/server-waking-up.png) | |

## Features

**Trips and stops**
- Create trips with dates, description, tags, and a cover color or cover photo
- Add stops by searching for a place (OpenStreetMap / Nominatim); each stop has a date, a note, and a type:
  overnight stay, passing through, or day trip
- The route is drawn on a map in the order of the visit; day trips are shown with their distance from the last
  overnight stop
- Route length and distance between stops, calculated from the coordinates
- Up to five photos per stop, a gallery with a large view, and a cover photo for the trip
- A short Wikipedia summary for every stop, with a link to the full article

**Overview**
- Search trips by place, country, or keyword; filter by year and tags
- **World map:** every visited country is colored, wishlist countries in a second color; clicking a country lists its
  trips and wishes
- **Statistics:** totals, records (longest trip, longest route, most stops), and breakdowns by year, country, and month

**Wishlist**
- Destinations with one or more countries, an optional place or region, a reason, a priority, and the best time to
  travel
- **Plan a trip:** turns a wish into a new trip with the destination already filled in

**Accounts and languages**
- Registration and login; every user sees and changes only their own data
- German and English user interface, including dates, numbers, country names, error messages, place search, and
  Wikipedia
- Light and dark design: follows the system setting, or switch with the sun / moon button

## Tech stack

| Area | Technologies |
|---|---|
| Frontend | React 19, TypeScript, Vite, React Router, Tailwind CSS v4, Leaflet with react-leaflet, axios |
| Backend | Java 25, Spring Boot 4.1 (Web MVC, Validation, Security, Data MongoDB, Actuator), Lombok, jjwt, springdoc-openapi (Swagger UI) |
| Database | MongoDB Atlas; photos in GridFS |
| External services | OpenStreetMap tiles, Nominatim place search, Wikipedia API (all free, no API key) |
| Quality | JUnit 5 with MockMvc and an embedded MongoDB, JaCoCo, SonarQube Cloud, oxlint |
| CI/CD | GitHub Actions, Docker, Docker Hub, Render |

## Architecture

### System overview

The backend and the built frontend run together in **one Docker container**. The browser loads the React app and
calls the REST API on the same address, so there is no CORS setup. Every endpoint under `/api` requires a JWT, except
registration and login. Every request also carries the language of the user interface. The API documentation
(Swagger UI) and the health check for Render are public.

```mermaid
flowchart LR
    user(["Browser<br/>React single-page app"])

    subgraph render["Render · Docker container"]
        direction TB
        static["Static frontend<br/>index.html, JS, CSS"]
        docs["Swagger UI · /swagger-ui.html<br/>Health check · /actuator/health"]
        subgraph api["Spring Boot REST API · /api"]
            direction TB
            security["Spring Security<br/>JwtFilter"]
            controllers["Controllers<br/>HTTP, validation"]
            services["Services<br/>business logic"]
            repos["Repositories<br/>Spring Data MongoDB"]
            security --> controllers --> services --> repos
        end
    end

    subgraph atlas["MongoDB Atlas"]
        direction TB
        collections[("users · trips · wishlist")]
        gridfs[("GridFS<br/>photos")]
    end

    subgraph external["External services, free"]
        direction TB
        osm["OpenStreetMap<br/>map tiles"]
        nominatim["Nominatim<br/>place search"]
        wikipedia["Wikipedia<br/>summaries, German and English"]
    end

    user -- "pages" --> static
    user -- "JSON + JWT + language" --> security
    user -- "API docs" --> docs
    user -- "tiles" --> osm
    repos --> collections
    repos --> gridfs
    services -- "HTTP" --> nominatim
    services -- "HTTP" --> wikipedia
```

The backend calls Nominatim and Wikipedia itself instead of the browser. That keeps the frontend simple, lets the
backend shape the answers, and keeps the external base URLs in configuration (`application.properties`), where the
tests can replace them. The backend remembers Wikipedia's answers in memory and sends one request at a time, because
Wikipedia slows down clients that send many requests in parallel.

### Backend layers

Each feature follows the same path: **Controller → Service → Repository**.

| Layer | Package | Responsibility |
|---|---|---|
| Controller | `controller` | HTTP mapping, request validation (`@Valid`), reads the logged-in user from the JWT |
| Service | `service` | Business rules: ownership checks, route and distance calculation, titles, photo handling |
| Repository | `repository` | MongoDB access through Spring Data; every query includes the user id |
| Model / DTO | `model`, `dto` | Java records: documents in the database, requests and responses of the API |

Errors are turned into a uniform JSON answer (`ProblemDetail`) in one place, `GlobalExceptionHandler`, so the
frontend can show field errors next to the right input. The texts come from `messages.properties` (German) or
`messages_en.properties` (English), depending on the language of the request.

### Login with JWT

```mermaid
sequenceDiagram
    actor U as User
    participant F as React app
    participant B as Spring Boot API
    participant D as MongoDB

    U->>F: Username and password
    F->>B: POST /api/auth/login
    B->>D: Find user, compare BCrypt hash
    B-->>F: JWT (signed with HMAC, valid 24 h)
    F->>F: Store token in localStorage
    U->>F: Open "Meine Reisen"
    F->>B: GET /api/trips with Authorization: Bearer token
    B->>B: JwtFilter checks the signature, reads the user id
    B->>D: Find trips of this user only
    B-->>F: Trips as JSON
```

The backend stays stateless: no sessions, no CSRF tokens. On a `401` answer the frontend removes the token and opens
the login page.

### Languages

The user interface is in German and English. The chosen language travels with every request, so the backend answers
in the same language and asks the external services in it.

```mermaid
flowchart LR
    switch(["DE | EN switch<br/>saved in the browser"])
    texts["Frontend texts<br/>i18n/texts.ts"]
    switch --> texts
    switch -- "Accept-Language: en" --> api["Spring Boot API"]
    api --> messages["Error messages<br/>messages_en.properties"]
    api --> nominatim["Nominatim<br/>accept-language=en"]
    api --> wiki["en.wikipedia.org"]
    wiki -.->|"no article named Venedig"| dewiki["de.wikipedia.org<br/>link to the English article"]
    dewiki -.->|"Venice"| wiki
```

- **Frontend:** all texts are in one file, German and English side by side (`cancel: {de: 'Abbrechen', en: 'Cancel'}`);
  TypeScript reports a text with only one language. Dates, numbers, and country names use the browser's `Intl` API.
- **Backend:** validation and error messages are keys that Spring looks up in `messages.properties` or
  `messages_en.properties`. Without the header, or for another language, everything stays German.
- **External services:** the place search returns names in the chosen language. A stop keeps the name it was saved
  with, so for a German name like "Venedig" the backend follows the language link of the German Wikipedia article to
  the English one, "Venice".
- The user's own content (titles, notes, the demo trips) is not translated.

### Data model

```mermaid
erDiagram
    USER ||--o{ TRIP : owns
    USER ||--o{ WISHLIST_ENTRY : owns
    TRIP ||--|{ STOP : "contains (embedded)"
    STOP }o--o{ PHOTO : "photoIds"
    TRIP |o--o| PHOTO : "coverPhotoId"

    USER {
        string id
        string username
        string passwordHash
    }
    TRIP {
        string id
        string userId
        string title
        date startDate
        date endDate
        list tags
    }
    STOP {
        string id
        string name
        point location
        string countryCode
        date date
        string type
    }
    WISHLIST_ENTRY {
        string id
        string userId
        string destination
        list countryCodes
        string priority
    }
    PHOTO {
        objectId id
        binary content
        string userId
        string tripId
    }
```

Stops are embedded in their trip, because they are always read and changed together with it. Photos are stored in
GridFS, the MongoDB storage for files, with the owner in their metadata.

### Frontend

| Folder | Content |
|---|---|
| `pages/` | One component per route: trips, trip detail, world map, statistics, wishlist, login, registration |
| `components/` | Reusable parts such as `TripMap`, `WorldMap`, `TripDialog`, `PhotoLightbox`, `WikipediaInfo` |
| `api/` | axios calls per resource; `client.ts` adds the token and the language, handles `401`, and notices slow answers |
| `i18n/` | All texts of the user interface, German and English side by side (`texts.ts`), and the chosen language |
| `utils/`, `types/` | Pure helper functions (filters, dates, countries) and TypeScript types of the API |

The Vite dev server forwards `/api` to the backend, so the frontend uses the same relative URLs locally and in
production.

Colors come from theme tokens in `index.css` (e.g. `surface`, `ink`, `accent`), each with a light and a dark value.
The sun / moon button puts the class `dark` on `<html>`, and every component changes with it, without dark mode code
of its own. A small script in `index.html` sets the design before the first paint, so a dark page does not flash
white.

### CI/CD pipeline

```mermaid
flowchart LR
    pr["Pull request"] --> build["Build and test backend<br/>(Maven, embedded MongoDB)"]
    pr --> sonar["SonarQube Cloud<br/>backend and frontend"]
    sonar --> gate{"Quality Gate<br/>and review"}
    build --> gate
    gate -- "merge to main" --> fe["Build frontend<br/>(npm ci, Vite)"]
    fe --> be["Build backend jar<br/>with the frontend inside"]
    be --> docker["Docker image<br/>to Docker Hub"]
    docker --> render["Render deploy hook<br/>new version live"]
```

`main` is protected: every change goes through a pull request with a passing build, a passing SonarQube Quality Gate,
and a review by a teammate. Merging deploys automatically.

More details are in the [documentation](#documentation).

## Documentation

The project was planned before it was built: requirements first, then page mockups, then the architecture. The
documents were kept up to date with every pull request.

| Document | Content |
|---|---|
| [`docs/requirements.md`](docs/requirements.md) | What the application should do: features, rules, non-functional requirements, non-goals |
| [`TravelBlog – Seitenentwürfe.html`](TravelBlog%20–%20Seitenentwürfe.html) | Page mockups of the trip overview, trip detail, world map, and wishlist (open the file in a browser) |
| [`docs/architecture.md`](docs/architecture.md) | Architecture decisions and their reasons, e.g. data model, maps, photos, security |
| [`docs/deployment.md`](docs/deployment.md) | How a deployment runs, required secrets and environment variables |
| [Swagger UI](https://travelblog-latest.onrender.com/swagger-ui.html) | The REST API, generated from the controllers, with every endpoint to try out |
| [`CLAUDE.md`](CLAUDE.md) | Working rules for the AI coding assistant (Claude Code): tech stack, commands, architecture and code quality rules, Git workflow |

## Project structure

```
TravelBlog/
├── backend/                 Spring Boot application
│   └── src/main/java/org/example/backend/
│       ├── config/          clients for Nominatim and Wikipedia, API documentation
│       ├── controller/      REST endpoints under /api
│       ├── dto/             request and response records
│       ├── exception/       uniform error answers
│       ├── model/           MongoDB documents
│       ├── repository/      Spring Data repositories
│       ├── security/        Spring Security and the JWT filter
│       └── service/         business logic
├── frontend/                React application (Vite)
│   └── src/                 pages, components, api, i18n, utils, types
├── docs/                    requirements, architecture, deployment, screenshots
├── .github/workflows/       CI, SonarQube analysis, deployment
└── Dockerfile               one image with backend and frontend
```

## Team

| | Main contributions |
|---|---|
| **Natalia Dybina** ([@NataliaDybina](https://github.com/NataliaDybina)) | Trip list with filters and tags, stops and stop types, trip route map, statistics page, Wikipedia integration, deployment pipeline |
| **Dinara Mufaizalova** ([@dinara-mfl](https://github.com/dinara-mfl)) | Registration and login with JWT, creating trips, trip detail page, photo upload with gallery and cover photo, CI and test coverage |
| **Frank David Lee** ([@codeByFrank](https://github.com/codeByFrank)) | Wishlist, world map with visited countries and wishes, "plan a trip" from a wish, demo account, deployment fixes |

## Running locally

### Prerequisites

- JDK 25
- Node.js with npm (a current LTS version)
- A MongoDB database: either installed locally or a free MongoDB Atlas cluster

### 1. Configure the environment variables

The backend needs two environment variables; without them it does not start:

| Variable | Purpose |
|---|---|
| `MONGODB_URI` | Connection string of the MongoDB database |
| `JWT_SECRET` | Key that signs the login tokens (JWT) |
| `DEMO_PASSWORD` | Optional: creates the demo user `demo` with sample data (see `docs/architecture.md` → Demo user) |

#### `MONGODB_URI`

The backend reads the MongoDB connection string from the environment variable `MONGODB_URI`
(`backend/src/main/resources/application.properties`: `spring.mongodb.uri=${MONGODB_URI}`).

The database name is part of the URI (the path after the host). If it is missing, MongoDB uses the database `test`,
so always include `/travelblog`.

Local MongoDB:

```
mongodb://localhost:27017/travelblog
```

MongoDB Atlas:

```
mongodb+srv://<user>:<password>@<cluster-host>/travelblog?retryWrites=true&w=majority
```

The URI may contain a password: never commit it, and do not put it into `application.properties`.

#### `JWT_SECRET`

Any random string of at least 32 characters (`app.jwt.secret=${JWT_SECRET}` in `application.properties`). Each
developer can use their own value locally. Generate one with:

```
openssl rand -base64 48
```

Treat it like a password: never commit it. If it changes, existing tokens become invalid and users have to log in
again. The tests use their own value from `backend/src/test/resources/application.properties`.

#### Setting the variables

Set the variables in one of these ways:

- **Terminal (macOS / Linux):** `export MONGODB_URI="mongodb://localhost:27017/travelblog"` and
  `export JWT_SECRET="<your secret>"`, then start the backend from the same terminal.
- **Terminal (Windows PowerShell):** `$env:MONGODB_URI="mongodb://localhost:27017/travelblog"` and
  `$env:JWT_SECRET="<your secret>"`
- **IntelliJ IDEA:** Run → Edit Configurations → `BackendApplication` → Environment variables →
  `MONGODB_URI=mongodb://localhost:27017/travelblog;JWT_SECRET=<your secret>`

Spring Boot does not read `.env` files; the variable has to be set in the environment of the process.

### 2. Start the backend

```
cd backend
./mvnw spring-boot:run
```

The backend runs on `http://localhost:8080`; all endpoints start with `/api` (e.g. `http://localhost:8080/api/trips`).

### 3. Start the frontend

```
cd frontend
npm install
npm run dev
```

Open the URL Vite prints (usually `http://localhost:5173`). The Vite dev server forwards every request to `/api` to
the backend on `http://localhost:8080`, so the backend has to be running.

All pages except login and registration need a logged-in user: register an account on `/register` first, then log
in. With `DEMO_PASSWORD` set, you can also log in as `demo`.

## Tests and checks

Backend (in `backend/`):

```
./mvnw test
```

The tests do not need `MONGODB_URI`, `JWT_SECRET`, or an installed MongoDB: they start an embedded MongoDB
(Flapdoodle) and use a test-only secret from `src/test/resources/application.properties`. The first run downloads the
MongoDB binary, so it needs internet access and takes a bit longer.

Frontend (in `frontend/`):

```
npm run lint
npm run build
```

The frontend has no automated tests (not part of the course); lint, type check, and build are its checks.

### Lighthouse

Measured on the live app with Lighthouse 13.6 ([PageSpeed Insights](https://pagespeed.web.dev/analysis/https-travelblog-latest-onrender-com-login/fqzrua4kiqn9m3a39ypuqq?form_factor=mobile)),
on 25 November 2026:

| | Performance | Accessibility | Best practices | SEO |
|---|---|---|---|---|
| Mobile (Moto G Power, slow 4G) | 100 | 100 | 100 | 100 |
| Desktop | 100 | 100 | 100 | 100 |

On mobile the first content appears after 0.9 s, with no blocking time and no layout shift. PageSpeed Insights can
only measure pages without a login, so these are the scores of the login page; the pages behind the login can be
measured in Chrome's developer tools (tab Lighthouse) while logged in.

## Working together

- Work on a feature branch, never directly on `main`; changes go in via pull requests.
- Never commit secrets (`MONGODB_URI` with a password, `JWT_SECRET`, `.env` files, tokens).
- Read `docs/requirements.md` and `docs/architecture.md` before implementing a feature; update `architecture.md` when a
  decision changes.

## Credits

- Map data © [OpenStreetMap](https://www.openstreetmap.org/copyright) contributors; place search by
  [Nominatim](https://nominatim.org/)
- Country borders from [Natural Earth](https://www.naturalearthdata.com/) (public domain)
- Place summaries from the German and English [Wikipedia](https://www.wikipedia.org/) (CC BY-SA 4.0)
- Photos of the demo trips from [Unsplash](https://unsplash.com/) (Unsplash License)
