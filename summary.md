# Spring Boot Seminar — One-Page Summary

A 4-day, lab-heavy Spring Boot seminar for 3rd-year engineering students. Builds the **Library Management System** twice — once thinly (Unit 1), then properly (Unit 2). 6 hours/day, 2 units, 12 labs, 1 capstone.

## Unit 1 — "Spring Boot in anger" (Days 1–2)

Get a REST API running and understand what Spring is actually doing under the hood. **No database yet.** Students leave Day 2 with a working in-memory CRUD API for `Book` and `Member`, deliberately messy and single-layered so Unit 2 has something to refactor. Topics: project bootstrap via `start.spring.io`, IoC and dependency injection, beans + `@Profile` + configuration, `@RestController`, REST verbs, status codes, `@PathVariable`/`@RequestParam`/`@RequestBody`, Jackson JSON serialization. Builds on the same Maven project across all six labs.

## Unit 2 — "Production-shape Spring Boot" (Days 3–4)

Rebuild the same domain (Library) as a production-grade app. **Fresh project**, layered architecture, real persistence. Adds: JPA + H2 (with MySQL via the `prod` profile), Controller → Service → Repository layering, DTOs separated from entities, Bean Validation (`@Valid`), structured error responses (`@RestControllerAdvice`), `@ManyToOne` relationships (Book ↔ Loan ↔ Member) with transactional borrow/return logic, slice tests (`@DataJpaTest`, `@WebMvcTest` with `@MockitoBean`), Spring Boot Actuator, HTTP Basic security on `/loans/**`, packaging as a fat jar, and a multi-stage `Dockerfile` for the capstone.

## The 12 labs at a glance

| # | Unit | Lab | Core concept | Time | Tag |
|---|------|-----|--------------|------|-----|
| 1 | 1 | Hello, Spring | Bootstrap a project; first `@RestController` | 75 min | `lab-01-end` |
| 2 | 1 | Beans & config | Constructor injection, `@Value`, `@Profile` | 75 min | `lab-02-end` |
| 3 | 1 | First resource | `GET /books` from in-memory list (JSON via Jackson) | 75 min | `lab-03-end` |
| 4 | 1 | Full Book CRUD | All verbs with correct status codes, `ResponseEntity` | 90 min | `lab-04-end` |
| 5 | 1 | Query-param search | `/books/search?title=&author=` with `@RequestParam` | 60 min | `lab-05-end` |
| 6 | 1 | Solo consolidation | Build `Member` CRUD mostly unaided | 75 min | `lab-06-end` |
| 7 | 2 | JPA refactor | `@Entity`, `JpaRepository`, H2, `data.sql`, H2 console | 90 min | `lab-07-end` |
| 8 | 2 | Layers, DTOs, errors | `BookService`, `BookDTO`, `@Valid`, `@RestControllerAdvice` | 90 min | `lab-08-end` |
| 9 | 2 | Relationships | `Member` + `Loan` with `@ManyToOne`, borrow/return | 90 min | `lab-09-end` |
| 10 | 2 | Testing | `@DataJpaTest` + `@WebMvcTest` (≥4 cases incl. 404 & 400) | 90 min | `lab-10-end` |
| 11 | 2 | Profiles, security, ops | `prod` profile (MySQL), Actuator, HTTP Basic on `/loans` | 75 min | `lab-11-end` |
| 12 | 2 | Capstone — package & ship | `mvn package`, multi-stage `Dockerfile`, run container, submit | 90 min | `lab-12-end` |

**Stretch tasks** are built into every lab handout so stronger students stay engaged: pagination, custom validators, file-mode H2, Testcontainers, JWT auth, Cloud Native Buildpacks, etc.

Reference solutions live in `code/unit1-library-inmem/` (Labs 1–6) and `code/unit2-library-jpa/` (Labs 7–12), tagged per lab.
