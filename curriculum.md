# Curriculum — Day-by-Day Overview

**Audience:** 3rd-year engineering students. Mixed Java background; assume the baseline is Java syntax + basic OOP only.
**Format:** 4 days × 6 hours/day, lab-heavy (~2 hrs lecture, ~4 hrs lab).
**Outcome:** A containerized Library Management System built twice — in-memory (Unit 1) and production-shape (Unit 2).

## How to use this document

The night before each day:

1. Skim that day's section here.
2. Open the two slide decks in `slides/` for the day. They're the visual companion.
3. Open the three lab handouts in `labs/` so you know what students will do and where they'll get stuck.
4. The reference code in `code/unit1-library-inmem/` and `code/unit2-library-jpa/` is tagged at the end of each lab (e.g., `git checkout lab-04-end`) — use these tags to demonstrate live during lectures.

Talking points in the day-by-day sections below are conversation starters, not lecture scripts.

## Daily rhythm

| Slot | Nominal duration | Activity |
|------|-------------------|----------|
| 1 | 90 min | Lecture 1 |
| 2 | 60–90 min | Lab A |
| — | 30 min | Break / lunch buffer |
| 3 | 60 min | Lecture 2 |
| 4 | 60–90 min | Lab B |
| 5 | 60–90 min | Lab C + wrap / Q&A |

3 labs per day × 4 days = 12 labs total. On Unit 2 days the formal wrap compresses into the final lab's debrief because labs are longer.

---

## Day 1 — "Hello, Spring Boot" (Unit 1 begins)

### Lecture 1 — Spring fundamentals (90 min)
Slides: `slides/day1-lecture1-spring-fundamentals.md`

Talking points:
- Java/Maven refresh — `pom.xml`, dependencies, `mvn package`, the `mvnw` wrapper.
- What Spring is (IoC + DI) versus what Spring Boot is (sensible defaults, starters, embedded server).
- `@SpringBootApplication` decoded as `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`.
- Walk through what `start.spring.io` generates.
- The seminar arc: Library Management System, built twice.

### Lab 1 — Hello, Spring (~75 min)
Handout: `labs/lab-01-hello-spring.md` · Checkpoint: `lab-01-end`

Generate the project, run it, expose `GET /hello`.

### Lecture 2 — Beans and configuration (60 min)
Slides: `slides/day1-lecture2-beans-and-config.md`

Talking points:
- Bean lifecycle. `@Component`/`@Service`/`@Repository`/`@Controller` as stereotype aliases.
- Constructor injection vs `@Autowired` field injection (why constructor wins).
- `application.properties`, `@Value("${...}")`, `@ConfigurationProperties`.
- Profiles: `@Profile` on beans + `application-{profile}.properties`.

### Lab 2 — Beans and configuration (~75 min)
Handout: `labs/lab-02-beans-and-config.md` · Checkpoint: `lab-02-end`

Inject a `GreetingService`, swap implementations via `@Profile`.

### Lab 3 — First resource (~75 min)
Handout: `labs/lab-03-first-resource.md` · Checkpoint: `lab-03-end`

Add `GET /books` returning an in-memory `List<Book>` as JSON.

---

## Day 2 — "Real REST API in memory" (Unit 1 ends)

### Lecture 3 — REST verbs and Spring MVC (90 min)
Slides: `slides/day2-lecture3-rest-verbs.md`

Talking points:
- HTTP verb semantics (idempotent, safe).
- Status codes that matter (200, 201 + Location, 204, 400, 404, 409).
- Spring MVC annotations: `@GetMapping`, `@PostMapping`, etc.
- `@PathVariable` vs `@RequestParam` vs `@RequestBody`.
- `ResponseEntity<T>` for explicit status/header control.
- Jackson JSON serialization (no annotations needed for POJOs).

### Lab 4 — Full Book CRUD (~90 min)
Handout: `labs/lab-04-full-crud.md` · Checkpoint: `lab-04-end`

GET/POST/PUT/DELETE with correct status codes.

### Lecture 4 — What's missing in a toy app (60 min)
Slides: `slides/day2-lecture4-whats-missing.md`

Talking points:
- The four gaps: no persistence, no validation, no error handling, no layers.
- Why each one matters in real apps.
- The Unit 2 target architecture (controller → service → repository → DB).
- DTO vs entity — why you separate.

### Lab 5 — Query-parameter search (~60 min)
Handout: `labs/lab-05-query-params.md` · Checkpoint: `lab-05-end`

Add `/books/search?title=&author=` with `@RequestParam`.

### Lab 6 — Member CRUD consolidation (~75 min)
Handout: `labs/lab-06-solo-consolidation.md` · Checkpoint: `lab-06-end`

Replicate the Book CRUD pattern for Member, mostly unaided.

---

## Day 3 — "Persistence and shape" (Unit 2 begins)

> Unit 2 starts a fresh project at `code/unit2-library-jpa/`. Unit 1 stays untouched.

### Lecture 5 — Layers and JPA (90 min)
Slides: `slides/day3-lecture5-layers-and-jpa.md`

Talking points:
- The layered architecture diagram.
- JPA vs Hibernate vs Spring Data JPA — what each layer does.
- `@Entity`, `@Id`, `@GeneratedValue`, `@Column`.
- `JpaRepository<E, ID>` and what it gives you for free.
- Derived query methods (`findByTitleContainingIgnoreCase...`).
- H2 — in-memory, file mode, the H2 console.
- `data.sql` for seed data.

### Lab 7 — JPA refactor (~90 min)
Handout: `labs/lab-07-jpa-refactor.md` · Checkpoint: `lab-07-end`

Convert `Book` to `@Entity`, introduce `BookRepository`, wire H2.

### Lecture 6 — Services, DTOs, validation (60 min)
Slides: `slides/day3-lecture6-services-dtos-validation.md`

Talking points:
- Service layer's job — business logic, transactions.
- `@Transactional` and why borrow-and-decrement-copies must be atomic.
- DTO vs entity — decoupling, security, validation.
- A manual `BookMapper`; mention MapStruct for real projects.
- Bean Validation annotations and `@Valid`.
- `@RestControllerAdvice` for structured errors.

### Lab 8 — Layers, DTOs, validation, errors (~90 min)
Handout: `labs/lab-08-layers-dtos-errors.md` · Checkpoint: `lab-08-end`

Insert `BookService`, `BookDTO`, `@Valid`, `GlobalExceptionHandler`.

### Lab 9 — Relationships (~90 min)
Handout: `labs/lab-09-relationships.md` · Checkpoint: `lab-09-end`

Add `Member` and `Loan` entities with `@ManyToOne`. Borrow/return endpoints with transactional logic.

---

## Day 4 — "Production-shape and ship" (Unit 2 ends)

### Lecture 7 — Testing (90 min)
Slides: `slides/day4-lecture7-testing.md`

Talking points:
- The testing pyramid.
- `@SpringBootTest` vs slice tests (`@WebMvcTest`, `@DataJpaTest`).
- `MockMvc` and `@MockitoBean` (the new `@MockBean`).
- AssertJ assertions.
- Test naming conventions.
- What to test — and what not to.
- TDD in passing.

### Lab 10 — Testing (~90 min)
Handout: `labs/lab-10-testing.md` · Checkpoint: `lab-10-end`

One `@DataJpaTest` + one `@WebMvcTest` with ≥4 cases (includes 404 and validation 400).

### Lecture 8 — Profiles, security, ops, Docker (60 min)
Slides: `slides/day4-lecture8-profiles-security-ops.md`

Talking points:
- Profile-specific properties files.
- Spring Boot Actuator (`health`, `info`, `metrics`).
- Spring Security in 60 seconds — `SecurityFilterChain`, HTTP Basic, when to disable CSRF.
- Packaging as a fat jar.
- Docker fundamentals — image vs container.
- Multi-stage Dockerfile.
- Where to go next (Spring Cloud, WebFlux, OAuth2, messaging, Flyway).

### Lab 11 — Profiles, security, ops (~75 min)
Handout: `labs/lab-11-profiles-security-ops.md` · Checkpoint: `lab-11-end`

Add `prod` profile (MySQL), enable Actuator, protect `/loans/**` with HTTP Basic.

### Lab 12 — Capstone: package and ship (~90 min)
Handout: `labs/lab-12-capstone-package-ship.md` · Checkpoint: `lab-12-end`

Build the jar. Write the Dockerfile. Build the image. Run the container. Push to GitHub. Submit.

## Evaluation

Capstone submissions are scored against the rubric in [`evaluation.md`](evaluation.md).
