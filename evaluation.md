# Capstone Evaluation Rubric

The capstone (Lab 12) is the seminar's only graded artifact. Students submit a public GitHub repository URL containing their Library Management System.

## Submission requirements

- Public GitHub repository.
- Contains a `README.md` with build and run instructions.
- Contains the full Spring Boot source.
- Tests pass: `./mvnw test` exits 0.
- Includes a working `Dockerfile`.
- `docker build` and `docker run` produce a running container exposing the API on port 8080.

If any of the above is missing, the submission is incomplete.

## Scoring (100 points total)

### Core functionality (40 pts)

| Item | Points |
|------|--------|
| `GET /books` returns the list | 4 |
| `GET /books/{id}` returns 200 for valid, 404 for missing | 4 |
| `POST /books` returns 201 with Location header | 4 |
| `PUT /books/{id}` updates correctly | 4 |
| `DELETE /books/{id}` returns 204 | 4 |
| Member endpoints work analogously | 8 |
| `POST /loans` with auth borrows correctly (copies decrement) | 6 |
| `PUT /loans/{id}/return` restores copies | 6 |

### Code quality (30 pts)

| Item | Points |
|------|--------|
| Layered architecture (controller / service / repository) | 8 |
| DTOs separated from entities | 6 |
| Validation annotations on DTOs, `@Valid` on request bodies | 6 |
| `@ControllerAdvice` returns structured error responses | 6 |
| Reasonable naming, no dead code, no console-print debugging | 4 |

### Testing (10 pts)

| Item | Points |
|------|--------|
| At least one `@DataJpaTest` | 3 |
| At least one `@WebMvcTest` | 3 |
| At least one test covering a 404 | 2 |
| At least one test covering a 400 validation failure | 2 |

### Operations (10 pts)

| Item | Points |
|------|--------|
| Actuator `/health` returns 200 | 3 |
| `prod` profile defined (whether or not MySQL is running locally) | 3 |
| `/loans/**` requires authentication | 4 |

### Docker (10 pts)

| Item | Points |
|------|--------|
| Dockerfile builds cleanly | 4 |
| Container runs and exposes the API on port 8080 | 4 |
| Documented `docker run` command in README | 2 |

## Stretch bonus (up to 10 extra points)

For each completed stretch task across the 12 labs (visible in commits or README): +1 point, capped at +10.

## Grading bands

| Total | Grade |
|-------|-------|
| 90–100+ | A |
| 80–89 | B |
| 70–79 | C |
| 60–69 | D |
| <60 | Incomplete; rework required |

## How to grade efficiently

For each submission:

1. **Clone the repo. `./mvnw test`.** Pass/fail determines tests-section points.
2. **`./mvnw spring-boot:run`** and run the verification curls from each lab handout. Track which endpoints return correct results.
3. **Read the source tree.** Layered architecture and DTO/entity separation are visible in 30 seconds — check `book/`, `service/`, `controller/` package structure.
4. **`docker build && docker run`.** If it builds and the container responds to `curl`, that's 8 of 10 Docker points.
5. Stretch tasks: scan commits and README — bonus points are easy to overlook if you don't look for them explicitly.

Plan ~15–20 minutes per submission for thorough grading.
