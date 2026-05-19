# Lab 7 — JPA Refactor

**Duration:** ~90 minutes
**Day:** 3, Slot 2

## Objective

Replace the in-memory `List<Book>` with a JPA-backed `BookRepository` using H2 as the database. This is the start of Unit 2 — a **new project** that we'll grow over Days 3 and 4 into a production-shape Library Management System.

## Prerequisites

Labs 1–6 complete.

## Starter state

None — generate a fresh project. (Unit 1's reference code stays untouched in `code/unit1-library-inmem/`.)

## Steps

1. Open https://start.spring.io. Same settings as before (Maven, Java 21, Spring Boot 3.5.x, package `com.example.library`), but this time add these dependencies:
   - **Spring Web**
   - **Spring Data JPA**
   - **H2 Database**
   - **Validation**

   Generate, download, unzip into your workspace as `unit2-library-jpa`.

2. Create package `com.example.library.book`.

3. Create `Book.java` as a JPA `@Entity`:

   ```java
   package com.example.library.book;

   import jakarta.persistence.*;

   @Entity
   @Table(name = "books")
   public class Book {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       @Column(nullable = false)
       private String title;

       @Column(nullable = false)
       private String author;

       @Column(unique = true)
       private String isbn;

       private int copies;

       public Book() {}

       public Book(String title, String author, String isbn, int copies) {
           this.title = title;
           this.author = author;
           this.isbn = isbn;
           this.copies = copies;
       }

       // getters and setters for every field
   }
   ```

4. Create `BookRepository.java`. Notice you do not implement methods — Spring Data generates the implementation at runtime from the interface and method names.

   ```java
   public interface BookRepository extends JpaRepository<Book, Long> {
       List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);
   }
   ```

5. Replace `BookController` to use the repository:

   ```java
   @RestController
   @RequestMapping("/books")
   public class BookController {

       private final BookRepository repository;

       public BookController(BookRepository repository) {
           this.repository = repository;
       }

       @GetMapping
       public List<Book> list() { return repository.findAll(); }

       @GetMapping("/{id}")
       public ResponseEntity<Book> getById(@PathVariable Long id) {
           return repository.findById(id)
                   .map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
       }

       @PostMapping
       public ResponseEntity<Book> create(@RequestBody Book book) {
           Book saved = repository.save(book);
           return ResponseEntity.created(URI.create("/books/" + saved.getId())).body(saved);
       }

       @PutMapping("/{id}")
       public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody Book updated) {
           return repository.findById(id).map(b -> {
               b.setTitle(updated.getTitle());
               b.setAuthor(updated.getAuthor());
               b.setIsbn(updated.getIsbn());
               b.setCopies(updated.getCopies());
               return ResponseEntity.ok(repository.save(b));
           }).orElseGet(() -> ResponseEntity.notFound().build());
       }

       @DeleteMapping("/{id}")
       public ResponseEntity<Void> delete(@PathVariable Long id) {
           if (!repository.existsById(id)) return ResponseEntity.notFound().build();
           repository.deleteById(id);
           return ResponseEntity.noContent().build();
       }

       @GetMapping("/search")
       public List<Book> search(@RequestParam(defaultValue = "") String title,
                                @RequestParam(defaultValue = "") String author) {
           return repository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author);
       }
   }
   ```

6. Configure H2 in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:h2:mem:librarydb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=

   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   spring.jpa.defer-datasource-initialization=true

   spring.sql.init.mode=always
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console
   ```

7. Create `src/main/resources/data.sql` with seed data:

   ```sql
   INSERT INTO books (title, author, isbn, copies) VALUES ('Clean Code', 'Robert C. Martin', '9780132350884', 3);
   INSERT INTO books (title, author, isbn, copies) VALUES ('Effective Java', 'Joshua Bloch', '9780134685991', 2);
   INSERT INTO books (title, author, isbn, copies) VALUES ('The Pragmatic Programmer', 'Andrew Hunt', '9780201616224', 5);
   ```

8. Run the app. Watch the console — you'll see Hibernate's `CREATE TABLE books` and the three `INSERT` statements.

## Verification

```bash
curl http://localhost:8080/books
# JSON array of three seeded books — but now they have real DB-generated ids
```

Open `http://localhost:8080/h2-console` in a browser:
- JDBC URL: `jdbc:h2:mem:librarydb`
- User: `sa`
- Password: (empty)

Click **Connect**, then run `SELECT * FROM books;` — you should see all three.

## Stretch task

Switch H2 from in-memory to file-mode by changing the URL to `jdbc:h2:file:./data/library`. Insert a book via POST. Stop the app, restart it, and confirm the book survives. (Important: with file-mode, the `data.sql` will keep re-inserting and may violate unique constraints — set `spring.sql.init.mode=never` after the first run, or use `INSERT IGNORE`-style logic.)

## Common pitfalls

- **`Table "BOOKS" not found`** in console — schema wasn't created. Check `spring.jpa.hibernate.ddl-auto=update` is set.
- **`data.sql` runs before tables exist** → seed inserts fail. Set `spring.jpa.defer-datasource-initialization=true` so the seed runs after Hibernate creates the schema.
- **H2 console returns 403** — Spring Security may block it. In Lab 11 we'll add explicit permissions; for now you have no Security on the classpath, so this shouldn't happen.
- **`UnsatisfiedDependencyException: BookRepository`** at startup — make sure the package `com.example.library.book` is under the root package `com.example.library` (so component scanning finds the repository).

## Checkpoint

Reference solution: `git checkout lab-07-end` inside `code/unit2-library-jpa/`.
