# Lab 12 — Capstone: Package and Ship

**Duration:** ~90 minutes
**Day:** 4, Slot 5

## Objective

Take everything you've built and ship it. Package the app as a self-contained jar, run it inside a Docker container, and submit your repository to GitHub.

## Prerequisites

Lab 11 complete.

## Starter state

`git checkout lab-11-end`.

## Steps

### Part A — Package as a jar

```bash
./mvnw clean package
```

This produces `target/library-0.0.1-SNAPSHOT.jar`. Confirm it runs standalone:

```bash
java -jar target/library-0.0.1-SNAPSHOT.jar
# Spring Boot starts. Hit /books in another terminal to verify.
```

Stop with Ctrl-C.

### Part B — Write the Dockerfile

Create `Dockerfile` at the project root (next to `pom.xml`):

```dockerfile
# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

This is a **multi-stage** build: stage 1 has the JDK and Maven to compile; stage 2 has only the JRE to run. The final image is significantly smaller because it ships no build tools.

Also create `.dockerignore` (skip files we don't want to copy into the build context):

```
target/
.git/
.idea/
*.iml
.DS_Store
```

### Part C — Build and run the container

```bash
docker build -t library-app:1.0 .
```

First-time build: this downloads Maven dependencies inside the container, which can take 5–10 minutes. Subsequent builds use cached layers and are fast.

```bash
docker run --rm -p 8080:8080 --name library-app library-app:1.0
```

In another terminal, verify:

```bash
curl http://localhost:8080/books
# JSON list of books from H2 — same as if you'd run it locally
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

Stop the container: `docker stop library-app` (or Ctrl-C in its terminal).

### Part D — Push to GitHub and submit

1. Create a **public** GitHub repository (e.g., `library-management-spring`).
2. Add a top-level `README.md` describing how to run it:

   ```markdown
   # Library Management

   A Spring Boot REST API built for the Spring Boot Seminar.

   ## Run locally
   ./mvnw spring-boot:run

   ## Run with Docker
   docker build -t library-app:1.0 .
   docker run --rm -p 8080:8080 library-app:1.0

   ## Endpoints
   - GET /books
   - POST /books (validated body)
   - ...
   ```

3. Push your project:

   ```bash
   git remote add origin https://github.com/<you>/library-management-spring.git
   git push -u origin main
   ```

4. Submit the repository URL to your instructor.

## Submission rubric

See [`../evaluation.md`](../evaluation.md).

## Stretch task

Replace the hand-written `Dockerfile` with Spring Boot's built-in Cloud Native Buildpacks support:

```bash
./mvnw spring-boot:build-image
```

This produces an OCI image without you writing any Dockerfile at all. The image is more optimized (layered jar, JVM tuning, non-root user). Compare the size to the multi-stage Dockerfile.

## Common pitfalls

- **`docker build` fails with "target/*.jar not found"** — you ran an old Dockerfile that copied `target/*.jar` from the host, but `target/` is in `.dockerignore`. Use the multi-stage Dockerfile above.
- **First-time build hangs at "Resolving Maven dependencies"** — it's downloading; give it 5–10 minutes the first time. Subsequent builds are seconds.
- **Container starts but `curl localhost:8080` connects to nothing** — you forgot `-p 8080:8080` on `docker run`, so the port isn't exposed to the host.
- **App starts then immediately exits** — likely a port conflict or a database connection failure. Run `docker logs library-app` to see the stack trace.

## Checkpoint

Reference solution: `git checkout lab-12-end`.

## You finished. What now?

The seminar covered the foundations of Spring Boot — REST controllers, JPA persistence, layered architecture, validation, error handling, testing, profiles, observability, security, and containerization. Real-world Spring includes much more:

- **Spring Cloud / microservices** — multiple Spring Boot apps talking to each other.
- **Reactive Spring (WebFlux)** — non-blocking I/O for high-concurrency apps.
- **OAuth2 / OIDC** — proper authentication via Keycloak, Auth0, etc.
- **Messaging** — Kafka, RabbitMQ, JMS integration.
- **Database migrations** — Flyway or Liquibase.
- **Observability** — Micrometer metrics, distributed tracing, structured logging.

Each is its own deep dive. The base you've built here is real Spring Boot — the same shape used in production at companies large and small. Good luck.
