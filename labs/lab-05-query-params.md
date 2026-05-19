# Lab 5 — Query-Parameter Search

**Duration:** ~60 minutes
**Day:** 2, Slot 4

## Objective

Add `GET /books/search?title=...&author=...` that filters the in-memory list using query parameters.

## Prerequisites

Lab 4 complete.

## Starter state

`git checkout lab-04-end`.

## Steps

1. Add a `search` method to `BookController`:

   ```java
   @GetMapping("/search")
   public List<Book> search(@RequestParam(required = false) String title,
                            @RequestParam(required = false) String author) {
       return books.stream()
               .filter(b -> title == null || b.getTitle().toLowerCase().contains(title.toLowerCase()))
               .filter(b -> author == null || b.getAuthor().toLowerCase().contains(author.toLowerCase()))
               .toList();
   }
   ```

2. Both parameters are optional (`required = false`). When omitted, the corresponding filter passes everything through.

3. Run the app and try the three variations below.

## Verification

```bash
curl 'http://localhost:8080/books/search?title=clean'
# returns books whose title contains "clean" (case-insensitive)

curl 'http://localhost:8080/books/search?author=bloch'
# returns books by Bloch

curl 'http://localhost:8080/books/search?title=java&author=bloch'
# returns books matching BOTH filters
```

## Stretch task

Extract the filter logic into a separate `BookSearchService` class (still in-memory). This sets you up for Day 3 — Lab 8 will refactor a similar split for the database-backed version.

## Common pitfalls

- **400 Bad Request when the query parameter is missing** — you forgot `required = false`. By default, `@RequestParam` is required.
- **Case-sensitive matches** — students often forget the `toLowerCase()` calls; demonstrate the difference by searching for `Clean` vs `clean`.
- **Always returns empty list** — likely a stream collected with `.collect(Collectors.toUnmodifiableList())` and then somehow mutated; the `.toList()` method (Java 16+) is fine.

## Checkpoint

Reference solution: `git checkout lab-05-end`.
