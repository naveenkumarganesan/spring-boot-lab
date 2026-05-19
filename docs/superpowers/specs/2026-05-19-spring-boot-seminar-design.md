# Spring Boot Seminar — 4-Day Curriculum Design

**Date:** 2026-05-19
**Audience:** 3rd-year engineering college students (mixed Java background, most at beginner-Java / no-web baseline; some stronger students)
**Format:** 4 days × 6 hours/day, lab-heavy (2 hrs lecture + 4 hrs lab per day)
**Structure:** 2 units, 2 days each, 12 labs total

---

## 1. Goals

By the end of the seminar, every student should be able to:

1. Generate a Spring Boot project from scratch and explain what each starter dependency contributes.
2. Build a CRUD REST API backed by a relational database, with layered architecture, validation, and structured error responses.
3. Write a slice test for a controller and a slice test for a repository, and run them via Maven.
4. Package a Spring Boot application as a runnable jar, run it in a Docker container, and switch profiles between local H2 and remote MySQL.
5. Submit a working capstone repository that demonstrates all of the above.

Stronger students should additionally complete the per-lab stretch tasks (pagination, OpenAPI UI, Testcontainers, multi-stage Dockerfile, etc.).

## 2. Audience Assumptions

- Mixed batch. The seminar is designed for the **C-level baseline**: knows Java syntax and basic OOP, no build-tool experience, no REST / web / SQL background.
- Stronger students (Java + Maven + REST familiar) are kept engaged through **stretch tasks** built into every lab.
- Every lab has a "checkpoint git tag" so a student who falls behind can reset to a known-good state and rejoin the next lab without permanent damage.

## 3. Overall Arc

Build a **Library Management System** twice — once thinly (Unit 1, in-memory) and once properly (Unit 2, with DB, layers, validation, tests, and packaging).

**Why the same domain twice:** students don't lose time re-learning a problem space when Unit 2 starts. They already know what a `Book`, `Member`, and `Loan` are. All their energy goes into new Spring concepts.

- **Unit 1 — "Spring Boot in anger" (Days 1–2):** Get a REST API running. Understand what Spring is actually doing. No database.
- **Unit 2 — "Production-shape Spring Boot" (Days 3–4):** Persistence (JPA + H2), layered architecture, validation, structured error handling, testing, plus light touches of profiles, security, observability, and Docker.

## 4. Daily Rhythm

Each 6-hour day allocates roughly **2 hours to lecture** and **~4 hours to lab** (including transition / Q&A / wrap-up). Lab durations are not fixed — Unit 1 labs run 60–75 min, Unit 2 labs 75–90 min as concepts get heavier. The day shape is:

| Slot | Nominal duration | Activity |
|------|-------------------|----------|
| 1 | 90 min | Lecture 1 |
| 2 | 60–90 min | Lab A |
| — | 30 min | Break / lunch buffer |
| 3 | 60 min | Lecture 2 |
| 4 | 60–90 min | Lab B |
| 5 | 60–90 min | Lab C + wrap / Q&A |

That yields **3 labs per day × 4 days = 12 labs**, matching the constraint. On Unit 2 days the formal wrap-up compresses into the final lab's debrief, since labs are longer.

## 5. Day-by-Day Breakdown

### Day 1 — "Hello, Spring Boot" (Unit 1 begins)

- **Lecture 1 (90 min):** Java/Maven refresh (POM, dependencies, lifecycle) → what Spring is → IoC and DI by hand (`new` vs container) → Spring Boot starters and `@SpringBootApplication`.
- **Lab 1:** Generate the project on `start.spring.io`, import in IntelliJ, run "Hello, World" `@RestController`, hit it with `curl` and a browser.
- **Lecture 2 (60 min):** Beans, component scanning, `@Component` / `@Service` / `@Autowired`, constructor injection. `application.properties` and `@Value`.
- **Lab 2:** Build a `GreetingService` injected into the controller. Read greeting prefix from `application.properties`. Switch between two beans using `@Profile`.
- **Lab 3:** Add `/books` endpoint backed by an in-memory `List<Book>` returning JSON. Concept: Spring MVC auto-converts to JSON via Jackson.

### Day 2 — "Real REST API in memory" (Unit 1 ends)

- **Lecture 3 (90 min):** REST verbs and Spring MVC annotations (`@GetMapping`, `@PostMapping`, `@PathVariable`, `@RequestParam`, `@RequestBody`), status codes, `ResponseEntity`.
- **Lab 4:** Full CRUD for `Book` in memory — list, get-by-id, create, update, delete. Proper status codes (201, 204, 404).
- **Lecture 4 (60 min):** What's missing in a "toy" app — no persistence, no validation, no error handling, no layers. Sets up Unit 2.
- **Lab 5:** Add `/books/search?title=...&author=...` (query params, filter logic in controller — deliberately messy so Day 3 has something to refactor).
- **Lab 6:** Add a second resource `Member` (in-memory) with CRUD. Consolidation lab — students do this mostly on their own.

### Day 3 — "Persistence and shape" (Unit 2 begins)

- **Lecture 5 (90 min):** The layered architecture (Controller → Service → Repository → Entity), why DTOs, JPA basics, Spring Data JPA repositories, H2 console.
- **Lab 7:** Refactor `Book` to a JPA `@Entity`, create `BookRepository extends JpaRepository`, wire H2, drop the in-memory list. Verify with H2 console.
- **Lecture 6 (60 min):** Service layer, DTOs vs entities, Bean Validation (`@Valid`, `@NotBlank`, `@Size`), global exception handling with `@ControllerAdvice`.
- **Lab 8:** Introduce `BookService`, `BookDTO`, `@Valid` on create/update, `@ControllerAdvice` returning a structured error JSON for 400/404.
- **Lab 9:** Add `Member` entity and a `Loan` entity linking the two (one-to-many: a Member has many Loans; each Loan references one Book). Endpoints to borrow and return a book.

### Day 4 — "Production-shape and ship" (Unit 2 ends)

- **Lecture 7 (90 min):** Testing pyramid in Spring — `@SpringBootTest` vs slice tests (`@WebMvcTest`, `@DataJpaTest`), MockMvc, AssertJ assertions.
- **Lab 10:** Write `@DataJpaTest` for `BookRepository` (custom finder method) and `@WebMvcTest` for `BookController` with MockMvc — at least 4 test cases including a 404 and a validation 400.
- **Lecture 8 (60 min):** Profiles (`dev` / `prod`), Actuator basics (`/health`, `/info`), packaging the jar, a quick look at Spring Security (HTTP Basic on one protected endpoint), Docker in 10 minutes.
- **Lab 11:** Enable Actuator, add a `prod` profile that switches H2 → MySQL via `application-prod.properties`, protect `/loans` with HTTP Basic in `SecurityConfig`. Run with `--spring.profiles.active=prod`.
- **Lab 12 (Capstone):** Build & run the app as a jar (`mvn package`), write a `Dockerfile`, build and run the container, hit the API. Submit the GitHub repo link.

## 6. Lab Catalogue

Each final lab handout will include: **Objective · Prerequisites · Starter state · Step-by-step instructions · Expected output (curl examples and screenshots) · Stretch task · Common pitfalls.**

### Unit 1 — In-memory Library API

| # | Lab | Core concept | Time |
|---|-----|--------------|------|
| 1 | Hello, Spring | Bootstrap a project, run a `@RestController`, hit it with curl | 75 min |
| 2 | Beans & config | Inject a service, read config from `application.properties`, switch beans with `@Profile` | 75 min |
| 3 | First resource | `GET /books` returning JSON from an in-memory list (Jackson auto-conversion) | 75 min |
| 4 | Full CRUD | `Book` CRUD with correct status codes, `@PathVariable`, `@RequestBody`, `ResponseEntity` | 90 min |
| 5 | Query params | `/books/search?title=&author=` — practice `@RequestParam`, deliberately controller-heavy | 60 min |
| 6 | Solo consolidation | Replicate the CRUD pattern for `Member` mostly unaided | 75 min |

### Unit 2 — Production-shape Library

| # | Lab | Core concept | Time |
|---|-----|--------------|------|
| 7 | JPA refactor | Convert `Book` to `@Entity`, add `JpaRepository`, wire H2, inspect via H2 console | 90 min |
| 8 | Layers, DTOs, errors | Insert `BookService`, separate `BookDTO`, add `@Valid` + `@ControllerAdvice` returning structured errors | 90 min |
| 9 | Relationships | `Member` and `Loan` entities, `@OneToMany` / `@ManyToOne`, borrow/return endpoints | 90 min |
| 10 | Testing | `@DataJpaTest` for a custom finder + `@WebMvcTest` with MockMvc, ≥4 cases incl. 404 and validation 400 | 90 min |
| 11 | Profiles, security, ops | `prod` profile (H2→MySQL), Actuator on, HTTP Basic protects `/loans` | 75 min |
| 12 | Capstone: package & ship | `mvn package`, write `Dockerfile`, build container, run, hit API, submit repo | 90 min |

**Total lab time:** ~16 hrs nominal across the 4 days (Day 1 ~225 min, Day 2 ~225 min, Day 3 ~270 min, Day 4 ~255 min). Unit 2 days deliberately extend into the wrap window because the labs need it; lecture timings hold firm so the labs absorb the variance.

## 7. Stretch Tasks (per lab, for stronger students)

Examples of stretch tasks built into the lab handouts:

- Lab 3: Add pagination via `Pageable` (without yet introducing Spring Data).
- Lab 4: Add HATEOAS-style links to responses.
- Lab 5: Refactor the search logic out of the controller into a helper class.
- Lab 7: Switch H2 to file-mode persistence and demonstrate data survival across restarts.
- Lab 8: Write a custom Bean Validation constraint (e.g., `@ValidISBN`).
- Lab 9: Add cascading delete behavior and demonstrate orphan-removal.
- Lab 10: Add an integration test using Testcontainers + MySQL.
- Lab 11: Use Spring Security with JWT instead of HTTP Basic.
- Lab 12: Multi-stage Dockerfile that produces a slim JRE image.

Each stretch task is independent — a stronger student can attempt the stretch without breaking the baseline lab solution.

## 8. Tooling

- **JDK:** 21 (LTS, current Spring Boot 3.x default)
- **Spring Boot:** 3.3.x
- **Build tool:** Maven
- **IDE:** IntelliJ IDEA Community Edition (primary); VS Code with Java extensions (fallback)
- **Database:** H2 in-memory for all baseline labs; one optional MySQL switch in Lab 11
- **Project bootstrapping:** `start.spring.io`
- **API testing:** `curl` from terminal (primary); browser for GETs; Postman or HTTPie acceptable
- **Version control:** Git + GitHub (capstone submission)
- **Containerization:** Docker Desktop on student machines

**Pre-seminar install checklist** (covered in README): JDK 21, Maven (optional — IntelliJ ships one), IntelliJ Community, Git, Docker Desktop, a GitHub account.

## 9. Deliverables

Artifacts produced during the implementation phase:

1. **`README.md`** — seminar overview, prerequisites, software install checklist students follow before Day 1.
2. **`curriculum.md`** — day-by-day breakdown with lecture talking points and timing.
3. **`labs/lab-NN-<slug>.md`** — one handout per lab (12 total) with objective, starter state, step-by-step instructions, expected output, stretch task, and common pitfalls.
4. **`code/`** — two reference Spring Boot projects:
   - `code/unit1-library-inmem/` — finished state at end of Day 2.
   - `code/unit2-library-jpa/` — finished state at end of Day 4 (with `Dockerfile` and tests).
   - Each lab tagged in git (e.g., `lab-04-end`) so students can jump to any checkpoint.
5. **`slides/`** — eight lecture decks, one per lecture slot.
6. **`evaluation.md`** — rubric for grading the Lab 12 capstone submission.

## 10. Repository Layout

```
spring-boot/
├── README.md
├── curriculum.md
├── evaluation.md
├── labs/
│   ├── lab-01-hello-spring.md
│   ├── lab-02-beans-and-config.md
│   ├── lab-03-first-resource.md
│   ├── lab-04-full-crud.md
│   ├── lab-05-query-params.md
│   ├── lab-06-solo-consolidation.md
│   ├── lab-07-jpa-refactor.md
│   ├── lab-08-layers-dtos-errors.md
│   ├── lab-09-relationships.md
│   ├── lab-10-testing.md
│   ├── lab-11-profiles-security-ops.md
│   └── lab-12-capstone-package-ship.md
├── slides/
│   ├── day1-lecture1-spring-fundamentals.md
│   ├── day1-lecture2-beans-and-config.md
│   ├── day2-lecture3-rest-verbs.md
│   ├── day2-lecture4-whats-missing.md
│   ├── day3-lecture5-layers-and-jpa.md
│   ├── day3-lecture6-services-dtos-validation.md
│   ├── day4-lecture7-testing.md
│   └── day4-lecture8-profiles-security-ops.md
└── code/
    ├── unit1-library-inmem/
    │   └── (Spring Boot Maven project, tagged by lab)
    └── unit2-library-jpa/
        └── (Spring Boot Maven project, tagged by lab)
```

## 11. Out of Scope

To keep the seminar realistic for 4 days, these are explicitly **not** covered (or only mentioned in passing):

- Reactive Spring (WebFlux).
- Spring Cloud / microservices / service discovery.
- Messaging (Kafka, RabbitMQ).
- OAuth2 / OIDC. Security is limited to HTTP Basic on a single endpoint.
- Frontend integration (no React/Angular/Thymeleaf — the API is the deliverable).
- Production deployment beyond a local Docker container (no Kubernetes, no cloud).
- Database migrations (Flyway/Liquibase) — Hibernate `ddl-auto: update` is good enough for the seminar timeframe.
- Performance tuning, caching, async processing.

These can be mentioned in Lecture 8 as "where to go next" so students know what they don't yet know.

## 12. Success Criteria

The seminar is successful if, on Day 4 evening:

- ≥80% of students have submitted a working capstone repository (Lab 12) — runs locally via Docker, exposes the documented endpoints, includes at least the Lab 10 tests passing.
- Every student can articulate the difference between `@Controller` and `@Service`, between an entity and a DTO, and between a slice test and a full `@SpringBootTest`.
- The stronger students have completed at least 6 stretch tasks across the 12 labs.

## 13. Next Step

After user approval of this spec, invoke the `writing-plans` skill to produce an implementation plan covering the ordered creation of the curriculum doc, lab handouts, slides, and the two reference code projects.
