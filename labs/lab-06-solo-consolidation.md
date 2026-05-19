# Lab 6 — Member CRUD (Solo Consolidation)

**Duration:** ~75 minutes
**Day:** 2, Slot 5

## Objective

Build a complete `Member` resource with the same CRUD endpoints as `Book` — mostly on your own. This lab is deliberately less hand-holding. You've seen the pattern. Now apply it.

## Prerequisites

Lab 5 complete.

## Starter state

`git checkout lab-05-end`.

## The Member resource

Create a new package `com.example.library.member`.

`Member` POJO fields:

| Field | Type | Notes |
|-------|------|-------|
| `id` | `Long` | Set by the server |
| `name` | `String` | |
| `email` | `String` | |
| `registeredOn` | `LocalDate` | If client omits, default to `LocalDate.now()` |

`MemberController` endpoints to expose (mirror the pattern from `BookController` in Lab 4):

| Verb | Path | Behavior |
|------|------|----------|
| GET | `/members` | List all members |
| GET | `/members/{id}` | Return one, 404 if missing |
| POST | `/members` | Create, return 201 with Location header |
| PUT | `/members/{id}` | Update name and email; 404 if missing |
| DELETE | `/members/{id}` | Remove, 204 on success, 404 if missing |

Seed the in-memory list with two members so `GET /members` returns something useful immediately.

## Verification

```bash
curl http://localhost:8080/members
# JSON array of two seeded members

curl -i -X POST http://localhost:8080/members \
    -H 'Content-Type: application/json' \
    -d '{"name":"Priya Sharma","email":"priya@example.com"}'
# HTTP/1.1 201 Created
# Body includes id and the auto-filled registeredOn

curl -i http://localhost:8080/members/999
# HTTP/1.1 404 Not Found
```

Both endpoints (`/books` and `/members`) should keep working independently — they live at different paths.

## Stretch task

Add `GET /members/search?email=...` returning members whose email contains the substring (case-insensitive). Same pattern as Lab 5.

## Common pitfalls

- **Endpoints collide with `/books`** — forgot `@RequestMapping("/members")` on the class. Spring then registers your methods at the default root.
- **`LocalDate` serializes as a weird array** — modern Spring Boot is fine, but older versions need `jackson-datatype-jsr310`. If you see `[2024,1,10]` in JSON output, that's the issue.
- **POST returns `registeredOn: null`** — your default-to-`LocalDate.now()` logic only runs when the field is null. Check the conditional.

## Checkpoint

Reference solution: `git checkout lab-06-end`.

## Why this lab matters

Repetition is how patterns become muscle memory. By the end of this lab, the CRUD shape of a Spring REST controller should feel routine. Unit 2 will replace the in-memory list with a real database — but the controller surface will look almost identical.
