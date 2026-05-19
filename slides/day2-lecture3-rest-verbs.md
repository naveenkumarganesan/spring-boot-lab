---
title: Day 2 · Lecture 3 — REST verbs and Spring MVC
duration: 90 min
---

# Day 2 · Lecture 3
## REST, HTTP verbs, and Spring MVC annotations

---

## REST in one sentence

> Resources have URLs. HTTP verbs act on them.

A REST API is a set of URLs, each representing a "thing," and HTTP methods that read or modify those things.

| URL | Thing |
|------|-------|
| `/books` | The collection of all books |
| `/books/1` | The book with id 1 |
| `/books/1/loans` | Loans associated with book 1 |

---

## HTTP method semantics

| Verb | Purpose | Idempotent? | Safe? |
|------|---------|--------------|-------|
| `GET` | Read | Yes | Yes (no side effects) |
| `POST` | Create | No | No |
| `PUT` | Replace | Yes | No |
| `PATCH` | Partial update | No (typically) | No |
| `DELETE` | Remove | Yes | No |

**Idempotent** = calling twice has the same effect as calling once. **Safe** = the call doesn't modify state.

---

## Status codes that matter

| Code | Meaning | When to return |
|------|---------|----------------|
| 200 | OK | Successful GET / PUT |
| 201 | Created | POST that creates a resource — include `Location` header |
| 204 | No Content | Successful DELETE; body intentionally empty |
| 400 | Bad Request | Client sent garbage (validation failure) |
| 401 | Unauthorized | Auth required, not provided |
| 403 | Forbidden | Auth provided, but not allowed |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Business rule violated (e.g., duplicate ISBN) |
| 500 | Server Error | We broke something |

---

## Spring MVC controller annotations

```java
@RestController                              // = @Controller + @ResponseBody
@RequestMapping("/books")
public class BookController {

    @GetMapping                              // GET /books
    public List<Book> list() { ... }

    @GetMapping("/{id}")                     // GET /books/123
    public Book getById(@PathVariable Long id) { ... }

    @PostMapping                             // POST /books
    public Book create(@RequestBody Book b) { ... }

    @PutMapping("/{id}")                     // PUT /books/123
    public Book update(@PathVariable Long id, @RequestBody Book b) { ... }

    @DeleteMapping("/{id}")                  // DELETE /books/123
    public void delete(@PathVariable Long id) { ... }
}
```

---

## `@PathVariable` vs `@RequestParam` vs `@RequestBody`

| Annotation | Where the data comes from | Example URL |
|-------------|----------------------------|--------------|
| `@PathVariable` | A piece of the URL path | `/books/42` → `id = 42` |
| `@RequestParam` | A query parameter | `/books?author=bloch` → `author = "bloch"` |
| `@RequestBody` | The HTTP request body | POST/PUT JSON body |

Rule of thumb: path → identifies the resource; query → filters or modifies the operation; body → carries the resource's data.

---

## `ResponseEntity<T>` — explicit control

The Spring-default return is "200 OK with the value as JSON." That's fine for happy paths. When you need other status codes or headers, wrap the return value:

```java
@PostMapping
public ResponseEntity<Book> create(@RequestBody Book book) {
    Book saved = repository.save(book);
    return ResponseEntity
            .created(URI.create("/books/" + saved.getId()))   // 201 + Location
            .body(saved);
}

@GetMapping("/{id}")
public ResponseEntity<Book> getById(@PathVariable Long id) {
    return repository.findById(id)
            .map(ResponseEntity::ok)                          // 200
            .orElseGet(() -> ResponseEntity.notFound().build()); // 404
}
```

---

## JSON serialization: Jackson

Spring Boot includes **Jackson** by default. Any POJO with getters serializes to JSON automatically:

```java
public class Book {
    private Long id;
    private String title;
    // getters
}
```

Returned by a controller method as JSON:

```json
{ "id": 1, "title": "Clean Code" }
```

No annotations needed. `LocalDate`, `Instant`, etc. all serialize correctly out of the box.

---

## Content negotiation

The HTTP `Accept` header tells the server what format the client wants:

```
Accept: application/json
Accept: application/xml     (works if you add a Jackson XML dependency)
Accept: */*                  (default — server picks)
```

Spring picks an appropriate `HttpMessageConverter` based on what's on the classpath.

---

## What's missing in our app so far

By end of Lab 4, you'll have a full CRUD `/books` API — but:

- ✗ No database (everything is in memory; restart loses data).
- ✗ No validation (you can POST nonsense and we save it).
- ✗ No error handling (errors leak stack traces).
- ✗ Business logic and persistence are mixed into the controller.

That's the wall we hit on Day 2. Unit 2 (Day 3+) fixes all of these — with JPA, DTOs, validation, and layers.

---

## Lab 4 preview

Implement full CRUD for `Book`:

- 200 on success, 404 on miss.
- POST returns 201 + `Location: /books/4`.
- DELETE returns 204 No Content.

```bash
$ curl -i -X POST http://localhost:8080/books \
       -H 'Content-Type: application/json' \
       -d '{"title":"Test","author":"X","isbn":"1","copies":1}'
HTTP/1.1 201 Created
Location: /books/4
```

---

## Questions?
