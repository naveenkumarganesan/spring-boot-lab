---
title: Day 3 · Lecture 6 — Services, DTOs, validation
duration: 60 min
---

# Day 3 · Lecture 6
## Service layer, DTOs, validation, error handling

---

## The service layer's job

- **Business logic.** "A loan can only happen if the book has copies > 0."
- **Orchestration.** Talks to multiple repositories.
- **Transactions.** Defines what "atomic" means — borrow-and-decrement-copies must be one transaction.

```java
@Service
public class LoanService {
    @Transactional
    public Loan borrow(Long bookId, Long memberId) {
        Book book = books.findById(bookId).orElseThrow(...);
        if (book.getCopies() <= 0) throw new BorrowNotAllowedException(...);
        book.setCopies(book.getCopies() - 1);
        books.save(book);
        return loans.save(new Loan(book, member, LocalDate.now()));
    }
}
```

---

## `@Transactional` — what it actually does

Around the method:

```
TX.begin();
try {
    method();
    TX.commit();
} catch (RuntimeException e) {
    TX.rollback();
    throw e;
}
```

You get atomicity for free. If `loans.save()` fails, the `book.setCopies()` change rolls back too.

Without `@Transactional`, each `save()` is its own little transaction — book change might commit, loan creation might fail, copies count is wrong forever.

---

## DTO vs Entity

| Concern | Entity | DTO |
|---------|--------|-----|
| Lives in | Persistence layer | API layer |
| Has | `@Entity`, `@Id`, `@Column`, JPA stuff | Maybe `@NotBlank`, `@Email`, etc. |
| Knows about | The database | HTTP / JSON |
| Changes with | DB schema | API contract |

A `Book` entity may have an `internalNotes` field you never want clients to see. A `BookDTO` omits it.

---

## Mapping between them

The dumb way (and that's fine for a seminar):

```java
@Component
public class BookMapper {
    public BookDTO toDto(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        ...
        return dto;
    }

    public Book toEntity(BookDTO dto) { ... }
}
```

The fancy way: **MapStruct** generates the mapping code at compile time. Worth introducing in real projects, overkill for a 4-day seminar.

---

## Bean Validation — the annotations

Spec from `jakarta.validation.constraints`. Most useful:

| Annotation | Checks |
|-------------|--------|
| `@NotNull` | Not null |
| `@NotBlank` | Not null, not empty, not whitespace-only |
| `@NotEmpty` | Not null, not empty (string/collection) |
| `@Size(min, max)` | Length / collection size |
| `@Min(n)`, `@Max(n)` | Numeric bounds |
| `@Email` | Valid email format |
| `@Pattern(regexp)` | Matches a regex |

Apply to fields of your DTO:

```java
public class BookDTO {
    @NotBlank @Size(min = 1, max = 200) String title;
    @NotBlank @Size(min = 1, max = 100) String author;
    @Size(min = 10, max = 13) String isbn;
    @Min(0) int copies;
}
```

---

## Triggering validation

```java
@PostMapping
public BookDTO create(@Valid @RequestBody BookDTO dto) { ... }
```

`@Valid` tells Spring: validate the body before invoking my method. If anything fails, Spring throws `MethodArgumentNotValidException` *before* the method body runs.

---

## Catching that exception: `@ControllerAdvice`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(404)
                .body(new ErrorResponse(404, ex.getMessage(), Instant.now(), List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // build fieldErrors list, return 400
    }
}
```

`@RestControllerAdvice` runs once, applies to every controller, catches exceptions, returns structured JSON.

---

## Why a `record` for `ErrorResponse`

Records (Java 14+) are concise, immutable data carriers:

```java
public record ErrorResponse(
    int status,
    String message,
    Instant timestamp,
    List<Map<String, String>> fieldErrors
) {}
```

Auto-generated:
- Constructor
- Accessors (`status()`, `message()`, ...)
- `equals`, `hashCode`, `toString`

Perfect for DTOs / API response shapes. Jackson handles them out of the box.

---

## What a good error response looks like

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-05-19T11:32:01Z",
  "fieldErrors": [
    {"field": "title",  "message": "must not be blank"},
    {"field": "isbn",   "message": "size must be between 10 and 13"}
  ]
}
```

Structured. Predictable. The client knows exactly what to show the user.

---

## Lab 8 preview

- Introduce `BookDTO`, `BookMapper`, `BookService`.
- Add validation annotations.
- Add `GlobalExceptionHandler`.
- Refactor `BookController` to delegate to the service.

```bash
$ curl -i -X POST localhost:8080/books -d '{"title":""}'
HTTP/1.1 400 Bad Request
{"status":400,"message":"Validation failed",...,"fieldErrors":[{"field":"title","message":"must not be blank"},...]}
```

---

## Questions?
