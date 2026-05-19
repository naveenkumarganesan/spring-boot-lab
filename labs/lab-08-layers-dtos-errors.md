# Lab 8 — Layers, DTOs, Validation, and Error Handling

**Duration:** ~90 minutes
**Day:** 3, Slot 4

## Objective

Refactor the Unit 2 app to the layered architecture you'd see in real code:

- A **service layer** between the controller and the repository, with all business logic.
- A **DTO** so the API surface is decoupled from the database entity.
- **Bean Validation** annotations on the DTO, enforced via `@Valid`.
- A **global exception handler** that returns structured JSON for 404s and 400s — no more leaking stack traces.

## Prerequisites

Lab 7 complete.

## Starter state

`git checkout lab-07-end`.

## Steps

1. **Create `BookDTO`** in the `book` package — same fields as `Book`, plus validation annotations:

   ```java
   public class BookDTO {
       private Long id;

       @NotBlank
       @Size(min = 1, max = 200)
       private String title;

       @NotBlank
       @Size(min = 1, max = 100)
       private String author;

       @Size(min = 10, max = 13)
       private String isbn;

       @Min(0)
       private int copies;

       // getters and setters
   }
   ```

2. **Create `BookMapper`** — a simple manual mapper between entity and DTO:

   ```java
   @Component
   public class BookMapper {
       public BookDTO toDto(Book book) { /* copy fields */ }
       public Book toEntity(BookDTO dto) { /* copy fields */ }
   }
   ```

3. **Create a `common` package** to hold cross-cutting types.

4. **`BookNotFoundException`** in `common`:

   ```java
   public class BookNotFoundException extends RuntimeException {
       public BookNotFoundException(Long id) {
           super("Book not found: " + id);
       }
   }
   ```

5. **`ErrorResponse`** — a record returned for every error:

   ```java
   public record ErrorResponse(
       int status,
       String message,
       Instant timestamp,
       List<Map<String, String>> fieldErrors
   ) {}
   ```

6. **`GlobalExceptionHandler`** with `@RestControllerAdvice`. Handle two exceptions:

   - `BookNotFoundException` → 404 with the exception message.
   - `MethodArgumentNotValidException` (thrown when `@Valid` fails) → 400 with a `fieldErrors` array listing every failed field.

   ```java
   @RestControllerAdvice
   public class GlobalExceptionHandler {
       @ExceptionHandler(BookNotFoundException.class)
       public ResponseEntity<ErrorResponse> handleNotFound(BookNotFoundException ex) { ... }

       @ExceptionHandler(MethodArgumentNotValidException.class)
       public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) { ... }
   }
   ```

7. **`BookService`** — moves business logic out of the controller. Throws `BookNotFoundException` for misses.

   ```java
   @Service
   public class BookService {
       private final BookRepository repository;
       private final BookMapper mapper;

       public BookService(BookRepository repository, BookMapper mapper) { ... }

       public List<BookDTO> listAll() { ... }
       public BookDTO get(Long id) { ... }
       public BookDTO create(BookDTO dto) { ... }
       public BookDTO update(Long id, BookDTO dto) { ... }
       public void delete(Long id) { ... }
       public List<BookDTO> search(String title, String author) { ... }
   }
   ```

8. **Refactor `BookController`** to delegate to the service. Add `@Valid` to every `@RequestBody`:

   ```java
   @PostMapping
   public ResponseEntity<BookDTO> create(@Valid @RequestBody BookDTO dto) { ... }
   ```

9. Run the app.

## Verification

```bash
# 404 with structured body
curl -i http://localhost:8080/books/999
# HTTP/1.1 404 Not Found
# {"status":404,"message":"Book not found: 999","timestamp":"...","fieldErrors":[]}

# 400 with field-level details
curl -i -X POST http://localhost:8080/books \
    -H 'Content-Type: application/json' \
    -d '{"title":"","author":"X","isbn":"123","copies":1}'
# HTTP/1.1 400 Bad Request
# {"status":400,"message":"Validation failed","timestamp":"...",
#  "fieldErrors":[{"field":"title","message":"must not be blank"},
#                  {"field":"isbn","message":"size must be between 10 and 13"}]}
```

## Stretch task

Write a **custom Bean Validation annotation** `@ValidISBN`. It should accept either a 10-digit or a 13-digit string and reject anything else. Apply it to `BookDTO.isbn` instead of `@Size(min=10, max=13)`.

## Common pitfalls

- **400 returns Spring's default error body, not your `ErrorResponse`** — you used `@ControllerAdvice` instead of `@RestControllerAdvice`. The former needs `@ResponseBody` on each handler method; the latter has it baked in.
- **`@Valid` on `@RequestBody` does nothing** — make sure `spring-boot-starter-validation` is on the classpath (it is, you added it in Lab 7).
- **Mixing entities and DTOs in service signatures** — pick one. The service should speak DTOs to callers and only deal with entities internally.

## Checkpoint

Reference solution: `git checkout lab-08-end`.

## Reflection

Compare your Lab 4 `BookController` to your Lab 8 `BookController`. The HTTP shape is identical, but the responsibilities have moved: validation is on the DTO, persistence is in the repository, business logic is in the service, error formatting is in the handler. Each piece can be tested in isolation. That's the point of layering.
