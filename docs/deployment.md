# Deployment

The application runs as one Docker container on Render (a hosting service). The backend delivers the API under
`/api` and the built React frontend from the same address, so frontend and backend need no extra setup to talk to each
other. The database is MongoDB Atlas.

## How a deployment runs

Every push to `main` (that is, every merged pull request) starts the workflow `.github/workflows/deploy.yml`:

1. **build-frontend:** installs the npm packages from `package-lock.json` (`npm ci --ignore-scripts`) and builds the
   frontend (`npm run build`).
2. **build-backend:** copies the frontend build into `backend/src/main/resources/static` and builds the backend with
   Maven. The result is `backend/target/travelBlog.jar` (name set by `<finalName>` in `backend/pom.xml`).
3. **push-to-docker-hub:** builds the Docker image from the `Dockerfile` in the repository root and pushes it to
   Docker Hub as `nataliadybina/travelblog:latest`.
4. **deploy-to-render:** calls the Render deploy hook; Render then pulls the new image and restarts the service.

The progress and errors of each run are shown in the Actions tab of the GitHub repository.

## Requirements

- A Docker Hub account with the repository `nataliadybina/travelblog`
- A Render web service that runs the image `docker.io/nataliadybina/travelblog:latest`
- A MongoDB Atlas database that accepts connections from Render. Render's free plan has no fixed IP address, so
  under "Network Access" in Atlas the address `0.0.0.0/0` has to be allowed; the database user and its password
  protect the access.

## Configuration

### GitHub repository secrets

Set under Settings → Secrets and variables → Actions. They are never committed.

| Secret | Purpose |
|---|---|
| `DOCKER_USERNAME` | Docker Hub user that pushes the image |
| `DOCKER_PASSWORD` | Docker Hub access token of that user |
| `RENDER_DEPLOY_HOOK` | Deploy hook URL of the Render service (Render → service → Settings → Deploy Hook) |

### Environment variables on Render

Set in the Render service under Environment. They are the same variables as in local development (see `README.md`).

| Variable | Purpose |
|---|---|
| `MONGODB_URI` | Connection string of the production database, including the database name `/travelblog` |
| `JWT_SECRET` | Key that signs the login tokens, at least 32 characters; a different value than locally |
| `DEMO_PASSWORD` | Password of the demo user `demo`, a demo-only password. With it, every start creates the demo user and resets its sample data (`DemoDataSeeder`, see `architecture.md` → Demo user). Without it there is no demo user |

The container listens on port 8080 (`EXPOSE 8080` in the `Dockerfile`). Render finds the port by itself; if it does
not, set the environment variable `PORT` to `8080` on Render.

### Health check on Render

In the Render service under Settings → Health Check Path, enter `/actuator/health`. Render then sends traffic to a
new version only when this path answers `200` with `{"status":"UP"}`, and restarts the service if it stops
answering. The path is public and shows no details (see `architecture.md` → API Documentation and Health Check).

## Updates

No manual steps: merging a pull request into `main` deploys the new version. A new environment variable has to be
added on Render before the pull request that needs it is merged.

## Notes

- The frontend pages (e.g. `/trips/123`) exist only in React Router. `FrontendController` in the backend answers
  these paths with `index.html`, so reloading a page works. A new page in the frontend needs its path there too.
- Only `/api/**` requires a login; the frontend files are public and contain no user data.
- The container runs as its own user `travelblog`, not as root.
- The job `build-backend` runs on `ubuntu-24.04`, not `ubuntu-latest`: the backend tests start an embedded MongoDB
  (7.0.4), and there is no download of it for Ubuntu 26.04.
- On Render's free plan the service sleeps after a while without visitors; the first request afterwards takes about
  a minute.
- Changing `JWT_SECRET` logs out all users.
- The link preview (Open Graph tags in `frontend/index.html`) uses the absolute address of the live app,
  `https://travelblog-latest.onrender.com/`, for the page and for its image `og-image.png`. If the address on Render
  changes, change it there too. LinkedIn keeps a preview for a few days; the Post Inspector
  (linkedin.com/post-inspector) loads it again.
- The demo user `demo` belongs to the application: do not register it as a normal user, because every start
  replaces its trips and wishlist.
