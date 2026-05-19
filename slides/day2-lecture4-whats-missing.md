---
title: Day 2 · Lecture 4 — What's missing in a toy app
duration: 60 min
---

# Day 2 · Lecture 4
## What's missing in a toy app — bridge to Unit 2

---

## What we have so far

By the end of Lab 4 / Lab 5, the app does:

- ✅ Full CRUD for `/books`
- ✅ Query parameter search
- ✅ Proper HTTP status codes
- ✅ Constructor injection, profiles, configuration

Cool. But:

---

## The four gaps

| Gap | Symptom | What we'll do |
|------|---------|---------------|
| **No persistence** | Restart the app → all data gone | Spring Data JPA + H2 (Lab 7) |
| **No validation** | POST `{"title":""}` succeeds | Bean Validation + `@Valid` (Lab 8) |
| **No error handling** | 500 with stack trace in JSON | `@ControllerAdvice` (Lab 8) |
| **No layers** | Business logic lives in the controller | Service layer + DTOs (Lab 8) |

---

## Gap 1 — Persistence

```bash
curl -X POST localhost:8080/books -d '{...}'   # 201 created
# Restart the app
curl localhost:8080/books                       # Your new book is gone
```

In-memory means **in-memory**. Every restart resets to the seed data.

**Fix:** A real database. We'll use **H2** — runs in-memory or in a file, has a web console, zero install.

---

## Gap 2 — Validation

```bash
curl -X POST localhost:8080/books \
     -H 'Content-Type: application/json' \
     -d '{"title":"", "author":"", "copies":-5}'
# 201 Created — accepts garbage
```

We have no checks. The DB will accept it. Tomorrow's bug report: "Why do we have books with no title?"

**Fix:** **Bean Validation** annotations on the DTO + `@Valid` on the request body. Spring rejects invalid input automatically.

---

## Gap 3 — Error handling

When something goes wrong:

```json
{
  "timestamp": "2026-05-19T...",
  "status": 500,
  "error": "Internal Server Error",
  "trace": "java.lang.NullPointerException: ...\n at ...\n at ..."
}
```

Spring's default response leaks **stack traces** to clients. That's bad both for security (attackers learn your internals) and UX (it's noise).

**Fix:** **`@ControllerAdvice`** — a centralized place to translate exceptions into clean, structured error responses.

---

## Gap 4 — No layers

Right now, the controller does **everything**:

- Parses HTTP
- Holds the data
- Filters it
- Returns it

Real apps split these:

```
HTTP   →  Controller  →  Service  →  Repository  →  Database
                       (business)    (queries)
```

Why? Because each layer has **one job** and can be tested in isolation. A service test doesn't need an HTTP server. A controller test doesn't need a database.

---

## DTOs — the API/DB boundary

Don't expose your `@Entity` over HTTP. Use a **DTO** instead.

```
[Client]  ⇄  BookDTO  ⇄  Controller  ⇄  Service  ⇄  Book (entity)  ⇄  DB
```

Why?

- **Decoupling.** DB schema can evolve without breaking the API.
- **Security.** Internal fields (e.g., `passwordHash`, `internalNotes`) don't accidentally leak.
- **Validation.** DTO carries validation annotations; entity stays clean.

---

## The Unit 2 target architecture

```
                       HTTP layer
            ┌────────────────────────────┐
            │      BookController        │
            └──────────────┬─────────────┘
                           │ BookDTO
            ┌──────────────▼─────────────┐
            │       BookService          │ ← business logic, @Transactional
            └──────────────┬─────────────┘
                           │ Book (entity)
            ┌──────────────▼─────────────┐
            │     BookRepository         │ ← extends JpaRepository
            └──────────────┬─────────────┘
                           │ SQL
            ┌──────────────▼─────────────┐
            │             DB             │ ← H2, MySQL via profile
            └────────────────────────────┘

      cross-cutting:  GlobalExceptionHandler  (catches exceptions, returns ErrorResponse)
```

We'll build this over Days 3–4, starting tomorrow morning.

---

## Lab 5 + Lab 6 preview

Today's last two labs consolidate Unit 1:

- **Lab 5:** Add `/books/search?title=&author=` (more `@RequestParam` practice).
- **Lab 6:** Build the entire `Member` resource mostly on your own — proving you've internalized the CRUD pattern.

Tomorrow we tear all that apart and rebuild it properly.

---

## Questions?
