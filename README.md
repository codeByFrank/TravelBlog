# TravelBlog

A personal travel diary: record trips with places on a map, photos, and notes, see routes and visited countries, and
keep a wishlist of destinations. Group project of three people in a Java + React course.

- `backend/` — Spring Boot (Java 25, Maven, MongoDB)
- `frontend/` — React + TypeScript + Vite
- `docs/` — requirements and architecture decisions

## Prerequisites

- JDK 25
- Node.js with npm (a current LTS version)
- A MongoDB database: either installed locally or a free MongoDB Atlas cluster

## Running locally

### 1. Configure the environment variables

The backend needs two environment variables; without them it does not start:

| Variable | Purpose |
|---|---|
| `MONGODB_URI` | Connection string of the MongoDB database |
| `JWT_SECRET` | Key that signs the login tokens (JWT) |

#### `MONGODB_URI`

The backend reads the MongoDB connection string from the environment variable `MONGODB_URI`
(`backend/src/main/resources/application.properties`: `spring.mongodb.uri=${MONGODB_URI}`). Without it, the backend
does not start.

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
again.

#### Setting the variables

Set both variables in one of these ways:

- **Terminal (macOS / Linux):** `export MONGODB_URI="mongodb://localhost:27017/travelblog"` and
  `export JWT_SECRET="<your secret>"`, then start the backend from the same terminal.
- **Terminal (Windows PowerShell):** `$env:MONGODB_URI="mongodb://localhost:27017/travelblog"` and
  `$env:JWT_SECRET="<your secret>"`
- **IntelliJ IDEA:** Run → Edit Configurations → `BackendApplication` → Environment variables →
  `MONGODB_URI=mongodb://localhost:27017/travelblog;JWT_SECRET=<your secret>`

Spring Boot does not read `.env` files; the variable has to be set in the environment of the process.

The backend also needs the environment variable `JWT_SECRET`: a random string of at least 32 characters used to sign
login tokens (e.g. generated with `openssl rand -base64 48`). Never commit it. Set it the same way as `MONGODB_URI`.
The tests use their own value from `backend/src/test/resources/application.properties`.

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
in.

## Tests and checks

Backend (in `backend/`):

```
./mvnw test
```

The tests do not need `MONGODB_URI`, `JWT_SECRET`, or an installed MongoDB: they start an embedded MongoDB
(Flapdoodle) and use a test-only secret from `src/test/resources/application.properties`. The first
run downloads the MongoDB binary, so it needs internet access and takes a bit longer.

Frontend (in `frontend/`):

```
npm run lint
npm run build
```

## Working together

- Work on a feature branch, never directly on `main`; changes go in via pull requests.
- Never commit secrets (`MONGODB_URI` with a password, `JWT_SECRET`, `.env` files, tokens).
- Read `docs/requirements.md` and `docs/architecture.md` before implementing a feature; update `architecture.md` when a
  decision changes.
