---
title: Day 4 · Lecture 8 — Profiles, security, ops, Docker
duration: 60 min
---

# Day 4 · Lecture 8
## Profiles, security, ops, and Docker

---

## Profiles recap

You've seen `@Profile` on beans. The same machinery applies to **properties files**:

- `application.properties` — always loaded.
- `application-dev.properties` — loaded only if `dev` is active.
- `application-prod.properties` — loaded only if `prod` is active.

Later files **override** earlier ones. So you put defaults in `application.properties` and only the differences in profile-specific files.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## Spring Boot Actuator

A set of built-in HTTP endpoints for operating the app:

| Endpoint | What it tells you |
|----------|--------------------|
| `/actuator/health` | "Am I up? Is the DB reachable?" |
| `/actuator/info` | App metadata (version, build, etc.) |
| `/actuator/metrics` | JVM metrics, request counts |
| `/actuator/env` | Active config (be careful — secrets!) |
| `/actuator/loggers` | Adjust log levels at runtime |

Only `health` and `info` are exposed by default. You opt in to others:

```properties
management.endpoints.web.exposure.include=health,info,metrics
```

---

## Spring Security in 60 seconds

What it gives you:

- **Authentication** — who is this request from?
- **Authorization** — what are they allowed to do?
- **CSRF protection** (for browser apps).
- **Session management** or stateless.

You configure it with a `SecurityFilterChain` bean:

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(c -> c.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/loans/**").authenticated()
            .anyRequest().permitAll())
        .httpBasic(b -> {});
    return http.build();
}
```

This says: `/loans/**` requires authentication, everything else is public, use HTTP Basic.

---

## Authentication mechanisms

| Mechanism | When you'd use it |
|-----------|-------------------|
| **HTTP Basic** | Simple demos, internal tools, server-to-server |
| **Session cookies** | Browser apps, classic web |
| **JWT** | Stateless APIs, mobile, microservices |
| **OAuth2 / OIDC** | "Login with Google / Microsoft / Keycloak" |

We're using HTTP Basic — easy to demo, easy to test with `curl -u user:pass`.

---

## CSRF — when you need it

CSRF (Cross-Site Request Forgery) protection prevents another site from making your browser submit a form to your app.

- **Browser apps:** keep CSRF on.
- **Stateless APIs (called by code, not browsers):** disable it.

Disabling CSRF without thinking is sometimes the right call. For our REST API, it is.

---

## Packaging — `mvn package`

```bash
./mvnw clean package
ls target/library-0.0.1-SNAPSHOT.jar
```

Spring Boot's Maven plugin builds a **"fat jar"** — your code, all dependencies, even an embedded Tomcat, all in one file. Run it:

```bash
java -jar target/library-0.0.1-SNAPSHOT.jar
```

No external server install. No WAR file. Just `java -jar`.

---

## Docker in 10 minutes

| Term | Meaning |
|------|---------|
| **Image** | A read-only filesystem snapshot — code + runtime + dependencies |
| **Container** | A running instance of an image |
| **Dockerfile** | The recipe for building an image |
| **Registry** | Storage for images (Docker Hub, ECR, GCR) |

---

## Multi-stage Dockerfile

```dockerfile
# Stage 1 — build with the full JDK + Maven
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw -B -DskipTests package

# Stage 2 — run with only the JRE
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

The final image has no Maven, no JDK source compiler, no test code — just the JRE and your jar.

---

## Docker commands you'll use

```bash
docker build -t library-app:1.0 .
docker run --rm -p 8080:8080 library-app:1.0
docker ps                # what's running
docker logs <container>  # see stdout/stderr
docker stop <container>
```

`-p 8080:8080` maps host port 8080 to container port 8080. Without it, your container is unreachable from the host.

---

## Where to go next

This seminar is a foundation. The Spring ecosystem is much larger:

- **Spring Cloud** — microservices, service discovery, config server, circuit breakers.
- **WebFlux** — reactive (non-blocking) Spring for high-concurrency apps.
- **Spring Security OAuth2 / OIDC** — proper auth via Keycloak, Auth0, Okta.
- **Spring Kafka / Spring AMQP** — messaging integrations.
- **Flyway / Liquibase** — database migrations (not `ddl-auto=update` in production).
- **Micrometer + Prometheus + Grafana** — production observability.

Each is its own deep dive. But the architecture you've built this week is genuinely the shape of real production Spring Boot apps.

---

## Lab 11 + Lab 12 preview

**Lab 11:** Add the `prod` profile (MySQL), enable Actuator, protect `/loans` with HTTP Basic.

**Lab 12 (capstone):** Build a jar. Write a Dockerfile. Build the image. Run the container. Push to GitHub. Submit.

---

## Closing

You started Monday morning with `start.spring.io`. By tonight you'll have a containerized REST API with persistence, validation, error handling, layered architecture, tests, security, observability, and a Docker deployment.

That's a real project. Put it on your résumé.

---

## Questions?
