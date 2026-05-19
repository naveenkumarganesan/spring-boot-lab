# Lab 4 — Full Book CRUD

**Duration:** ~90 minutes
**Day:** 2, Slot 2

## Objective

Extend `BookController` with full CRUD endpoints (Create / Read / Update / Delete), each returning the correct HTTP status code.

## Prerequisites

Lab 3 complete.

## Starter state

`git checkout lab-03-end`.

## HTTP cheat sheet

| Verb | Path | Status on success | Status on miss | What it does |
|------|------|-------------------|----------------|--------------|
| GET | `/books` | 200 OK | — | List all |
| GET | `/books/{id}` | 200 OK | 404 Not Found | Fetch one |
| POST | `/books` | 201 Created + Location | — | Create |
| PUT | `/books/{id}` | 200 OK | 404 Not Found | Replace |
| DELETE | `/books/{id}` | 204 No Content | 404 Not Found | Remove |

## Steps

1. Add an `AtomicLong idGenerator = new AtomicLong(4)` field to `BookController` (start at 4 since the seed list uses 1–3).

2. Add `getById`:

   ```java
   @GetMapping("/{id}")
   public ResponseEntity<Book> getById(@PathVariable Long id) {
       return findById(id)
               .map(ResponseEntity::ok)
               .orElseGet(() -> ResponseEntity.notFound().build());
   }
   ```

3. Add a private helper:

   ```java
   private Optional<Book> findById(Long id) {
       return books.stream().filter(b -> b.getId().equals(id)).findFirst();
   }
   ```

4. Add `create`:

   ```java
   @PostMapping
   public ResponseEntity<Book> create(@RequestBody Book book) {
       book.setId(idGenerator.getAndIncrement());
       books.add(book);
       return ResponseEntity.created(URI.create("/books/" + book.getId())).body(book);
   }
   ```

5. Add `update`:

   ```java
   @PutMapping("/{id}")
   public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody Book updated) {
       Optional<Book> existing = findById(id);
       if (existing.isEmpty()) return ResponseEntity.notFound().build();
       Book b = existing.get();
       b.setTitle(updated.getTitle());
       b.setAuthor(updated.getAuthor());
       b.setIsbn(updated.getIsbn());
       b.setCopies(updated.getCopies());
       return ResponseEntity.ok(b);
   }
   ```

6. Add `delete`:

   ```java
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> delete(@PathVariable Long id) {
       boolean removed = books.removeIf(b -> b.getId().equals(id));
       return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
   }
   ```

7. Run the app and verify each verb. The `-i` flag in curl shows the response headers and status line so you can confirm the status code.

## Verification

```bash
curl -i http://localhost:8080/books/1
# HTTP/1.1 200 OK
curl -i http://localhost:8080/books/999
# HTTP/1.1 404 Not Found
curl -i -X POST http://localhost:8080/books \
    -H 'Content-Type: application/json' \
    -d '{"title":"Test","author":"X","isbn":"1","copies":1}'
# HTTP/1.1 201 Created
# Location: /books/4
curl -i -X PUT http://localhost:8080/books/1 \
    -H 'Content-Type: application/json' \
    -d '{"title":"Clean Code 2e","author":"Robert C. Martin","isbn":"9780132350884","copies":4}'
# HTTP/1.1 200 OK
curl -i -X DELETE http://localhost:8080/books/2
# HTTP/1.1 204 No Content
curl -i -X DELETE http://localhost:8080/books/999
# HTTP/1.1 404 Not Found
```

## Stretch task

Return **409 Conflict** when a POST tries to insert a book whose ISBN already exists in the list.

## Common pitfalls

- **POST/PUT body is `null`** — you forgot `@RequestBody` on the parameter. Spring will silently bind nothing.
- **`Content-Type: application/json` missing on curl** — without it, Spring can't deserialize the body.
- **Status is always 200** — you returned the value directly instead of via `ResponseEntity`. Wrap it.

## Checkpoint

Reference solution: `git checkout lab-04-end`.
