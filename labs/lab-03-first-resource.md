# Lab 3 — First Resource (`/books`)

**Duration:** ~75 minutes
**Day:** 1, Slot 5

## Objective

Expose `GET /books` returning a list of `Book` objects as JSON. Spring MVC serializes POJOs to JSON automatically via Jackson.

## Prerequisites

Lab 2 complete.

## Starter state

`git checkout lab-02-end`.

## Steps

1. Create a new package `com.example.library.book`.
2. Create `Book.java` — a plain POJO with these fields and standard getters/setters:

   ```java
   public class Book {
       private Long id;
       private String title;
       private String author;
       private String isbn;
       private int copies;

       public Book() {}

       public Book(Long id, String title, String author, String isbn, int copies) {
           this.id = id;
           this.title = title;
           this.author = author;
           this.isbn = isbn;
           this.copies = copies;
       }

       // getters and setters for all fields
   }
   ```

   Use IntelliJ's "Generate" (Alt+Insert) → Getters and Setters to save typing.

3. Create `BookController.java`. Annotate with `@RestController` and `@RequestMapping("/books")`. Initialize an `ArrayList<Book>` field with three seed books. Add a single `@GetMapping` method returning the list:

   ```java
   @RestController
   @RequestMapping("/books")
   public class BookController {

       private final List<Book> books = new ArrayList<>(List.of(
           new Book(1L, "Clean Code", "Robert C. Martin", "9780132350884", 3),
           new Book(2L, "Effective Java", "Joshua Bloch", "9780134685991", 2),
           new Book(3L, "The Pragmatic Programmer", "Andrew Hunt", "9780201616224", 5)
       ));

       @GetMapping
       public List<Book> list() {
           return books;
       }
   }
   ```

4. Run the app and test:

   ```bash
   curl -s http://localhost:8080/books | jq .
   ```

   (Install `jq` for pretty-printing JSON; or just use `curl` without it.)

## Expected output

A JSON array of three books, each with `id`, `title`, `author`, `isbn`, `copies`.

```json
[
  {"id":1,"title":"Clean Code","author":"Robert C. Martin","isbn":"9780132350884","copies":3},
  {"id":2,"title":"Effective Java","author":"Joshua Bloch","isbn":"9780134685991","copies":2},
  {"id":3,"title":"The Pragmatic Programmer","author":"Andrew Hunt","isbn":"9780201616224","copies":5}
]
```

## Stretch task

Add pagination by hand: accept `?page=` and `?size=` query parameters and return a sublist of the books. (Real Spring pagination uses `Pageable` — we'll get there in Unit 2.)

## Common pitfalls

- **Empty JSON `{}` returned** — Jackson can't serialize a POJO without getters. Add public getters for every field.
- **404 on `/books`** — check the `@RequestMapping("/books")` annotation is on the class, not on the method. Inside, `@GetMapping` (no path) means "GET on the class-level path".

## Checkpoint

Reference solution: `git checkout lab-03-end`.
