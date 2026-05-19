# Spring Boot Seminar — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Produce a complete 4-day Spring Boot seminar kit (curriculum doc, 12 lab handouts, 8 lecture decks, 2 reference Spring Boot code projects with per-lab git tags, and an evaluation rubric) suitable for delivering to a 3rd-year engineering batch.

**Architecture:** The deliverable lives in `/Users/naveen/dev/workspaces/workshops/spring-boot/`. Top-level docs (README, curriculum, evaluation) frame the seminar. `labs/` contains 12 markdown handouts. `slides/` contains 8 lecture decks (markdown — convertible to PDF/reveal.js). `code/unit1-library-inmem/` and `code/unit2-library-jpa/` are working Spring Boot Maven projects whose state at the end of each lab is captured as a git tag (`lab-NN-end`). Lab handouts reference the corresponding code state.

**Tech Stack:** JDK 21, Spring Boot 3.3.x (latest 3.3 patch at time of execution), Maven, Spring Web, Spring Data JPA, H2, MySQL (via profile), Spring Security (HTTP Basic), Spring Boot Actuator, JUnit 5 + MockMvc, Docker.

**Executor assumptions:** The executor is comfortable with Spring Boot 3.x and Maven. Where idiomatic Spring code is obvious (getters/setters, standard CRUD repository methods), this plan specifies file paths, class structure, and key methods rather than every line. Non-obvious code (entity relationships, controller advice, security config, tests, Dockerfile) is shown verbatim.

**Spec reference:** `docs/superpowers/specs/2026-05-19-spring-boot-seminar-design.md`

---

## Phase A — Repository Scaffolding

### Task 1: Initialize repository and create top-level skeleton

**Files:**
- Create: `.gitignore`
- Create: `README.md`
- Create: `labs/.gitkeep`
- Create: `slides/.gitkeep`
- Create: `code/.gitkeep`

- [ ] **Step 1: Initialize git**

Run: `cd /Users/naveen/dev/workspaces/workshops/spring-boot && git init -b main`
Expected: "Initialized empty Git repository"

- [ ] **Step 2: Create `.gitignore`**

```gitignore
# Build artifacts
target/
*.class
*.jar
!**/.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
.vscode/
.project
.classpath
.settings/

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# Local env overrides
application-local.properties
.env
```

- [ ] **Step 3: Create README skeleton**

```markdown
# Spring Boot — 4-Day Seminar

A hands-on Spring Boot seminar designed for 3rd-year engineering students. Two units, 4 days, 12 labs, ending with a containerized Library Management System.

## Audience

Mixed Java background. The baseline assumes Java syntax and basic OOP only; stretch tasks in every lab keep stronger students engaged.

## Pre-seminar install checklist

Students must install the following before Day 1:

1. **JDK 21** — `java --version` should report 21.x. ([Adoptium Temurin 21](https://adoptium.net/))
2. **IntelliJ IDEA Community Edition** — free, best Spring support of the free IDEs.
3. **Git** — `git --version` should work.
4. **Docker Desktop** — required for Lab 12.
5. **A GitHub account** — capstone submission.

Maven is bundled with IntelliJ; a separate install is not required.

## Day-by-day overview

See [`curriculum.md`](curriculum.md).

## Lab handouts

See the [`labs/`](labs/) directory. Twelve labs, three per day.

## Reference code

- [`code/unit1-library-inmem/`](code/unit1-library-inmem/) — Unit 1 finished state.
- [`code/unit2-library-jpa/`](code/unit2-library-jpa/) — Unit 2 finished state.

Each lab's checkpoint is captured as a git tag (e.g., `lab-04-end`). To jump to the end-of-Lab-4 state:

```bash
cd code/unit1-library-inmem
git checkout lab-04-end
```

## Capstone evaluation

See [`evaluation.md`](evaluation.md).
```

- [ ] **Step 4: Create directory placeholders**

```bash
touch labs/.gitkeep slides/.gitkeep code/.gitkeep
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "chore: initialize seminar repo skeleton"
```

---

## Phase B — Reference Code: Unit 1 (In-Memory Library API)

> Each task in this phase builds on the previous one. Work inside `code/unit1-library-inmem/`. At the end of each task, tag the commit (e.g., `lab-01-end`). Use `git tag lab-NN-end` after committing.

### Task 2: Lab 1 reference code — Hello, Spring

**Goal:** A `start.spring.io`-generated Spring Boot project that exposes `GET /hello`.

**Files:**
- Create: `code/unit1-library-inmem/pom.xml`
- Create: `code/unit1-library-inmem/src/main/java/com/example/library/LibraryApplication.java`
- Create: `code/unit1-library-inmem/src/main/java/com/example/library/HelloController.java`
- Create: `code/unit1-library-inmem/src/main/resources/application.properties`

- [ ] **Step 1: Generate the project via `start.spring.io` CLI**

Run (from `/Users/naveen/dev/workspaces/workshops/spring-boot/code/`):

```bash
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.3.5 \
  -d baseDir=unit1-library-inmem \
  -d groupId=com.example \
  -d artifactId=library \
  -d name=library \
  -d packageName=com.example.library \
  -d javaVersion=21 \
  -d dependencies=web \
  -o unit1.zip && unzip unit1.zip -d . && rm unit1.zip
```

Expected: project tree at `code/unit1-library-inmem/`.

- [ ] **Step 2: Replace the generated controller stub with `HelloController.java`**

```java
package com.example.library;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
```

- [ ] **Step 3: Verify build and run**

```bash
cd code/unit1-library-inmem
./mvnw spring-boot:run
```

In another terminal: `curl http://localhost:8080/hello`
Expected: `Hello, Spring Boot!`

Stop the server with Ctrl-C.

- [ ] **Step 4: Commit and tag**

```bash
git add code/unit1-library-inmem
git commit -m "feat(unit1): lab 01 — hello spring controller"
git tag lab-01-end
```

---

### Task 3: Lab 2 reference code — Beans and configuration

**Goal:** Inject a `GreetingService`, read prefix from properties, switch implementations via `@Profile`.

**Files:**
- Create: `code/unit1-library-inmem/src/main/java/com/example/library/greeting/GreetingService.java`
- Create: `code/unit1-library-inmem/src/main/java/com/example/library/greeting/EnglishGreetingService.java`
- Create: `code/unit1-library-inmem/src/main/java/com/example/library/greeting/HindiGreetingService.java`
- Modify: `code/unit1-library-inmem/src/main/java/com/example/library/HelloController.java`
- Modify: `code/unit1-library-inmem/src/main/resources/application.properties`

- [ ] **Step 1: Define `GreetingService` interface**

```java
package com.example.library.greeting;

public interface GreetingService {
    String greet(String name);
}
```

- [ ] **Step 2: English implementation (default profile)**

```java
package com.example.library.greeting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"default", "en"})
public class EnglishGreetingService implements GreetingService {

    private final String prefix;

    public EnglishGreetingService(@Value("${greeting.prefix:Hello}") String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String greet(String name) {
        return prefix + ", " + name + "!";
    }
}
```

- [ ] **Step 3: Hindi implementation**

```java
package com.example.library.greeting;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("hi")
public class HindiGreetingService implements GreetingService {

    @Override
    public String greet(String name) {
        return "Namaste, " + name + "!";
    }
}
```

- [ ] **Step 4: Update controller to inject and use the service**

```java
package com.example.library;

import com.example.library.greeting.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final GreetingService greetingService;

    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return greetingService.greet(name);
    }
}
```

- [ ] **Step 5: Set prefix in `application.properties`**

```properties
greeting.prefix=Hello
```

- [ ] **Step 6: Verify default profile**

```bash
./mvnw spring-boot:run
curl http://localhost:8080/hello/Asha
```

Expected: `Hello, Asha!`

- [ ] **Step 7: Verify Hindi profile**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=hi
curl http://localhost:8080/hello/Asha
```

Expected: `Namaste, Asha!`

- [ ] **Step 8: Commit and tag**

```bash
git add code/unit1-library-inmem
git commit -m "feat(unit1): lab 02 — bean injection and @Profile"
git tag lab-02-end
```

---

### Task 4: Lab 3 reference code — First resource (`/books` GET)

**Goal:** Expose `GET /books` returning an in-memory list as JSON.

**Files:**
- Create: `code/unit1-library-inmem/src/main/java/com/example/library/book/Book.java`
- Create: `code/unit1-library-inmem/src/main/java/com/example/library/book/BookController.java`

- [ ] **Step 1: Create `Book` POJO**

```java
package com.example.library.book;

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

    // Standard getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }
}
```

- [ ] **Step 2: Create `BookController` with in-memory list**

```java
package com.example.library.book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

- [ ] **Step 3: Verify**

```bash
./mvnw spring-boot:run
curl http://localhost:8080/books
```

Expected: JSON array of three books.

- [ ] **Step 4: Commit and tag**

```bash
git add code/unit1-library-inmem
git commit -m "feat(unit1): lab 03 — GET /books from in-memory list"
git tag lab-03-end
```

---

### Task 5: Lab 4 reference code — Full Book CRUD

**Goal:** Complete CRUD endpoints with correct status codes.

**Files:**
- Modify: `code/unit1-library-inmem/src/main/java/com/example/library/book/BookController.java`

- [ ] **Step 1: Replace `BookController` with full CRUD**

```java
package com.example.library.book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/books")
public class BookController {

    private final List<Book> books = new ArrayList<>(List.of(
            new Book(1L, "Clean Code", "Robert C. Martin", "9780132350884", 3),
            new Book(2L, "Effective Java", "Joshua Bloch", "9780134685991", 2),
            new Book(3L, "The Pragmatic Programmer", "Andrew Hunt", "9780201616224", 5)
    ));
    private final AtomicLong idGenerator = new AtomicLong(4);

    @GetMapping
    public List<Book> list() {
        return books;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        book.setId(idGenerator.getAndIncrement());
        books.add(book);
        return ResponseEntity.created(URI.create("/books/" + book.getId())).body(book);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = books.removeIf(b -> b.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Optional<Book> findById(Long id) {
        return books.stream().filter(b -> b.getId().equals(id)).findFirst();
    }
}
```

- [ ] **Step 2: Verify each verb with curl**

```bash
./mvnw spring-boot:run
curl -i http://localhost:8080/books/1                                  # 200
curl -i http://localhost:8080/books/999                                # 404
curl -i -X POST http://localhost:8080/books \
    -H 'Content-Type: application/json' \
    -d '{"title":"Test","author":"X","isbn":"1","copies":1}'           # 201, Location header
curl -i -X PUT http://localhost:8080/books/1 \
    -H 'Content-Type: application/json' \
    -d '{"title":"Clean Code 2e","author":"Robert C. Martin","isbn":"9780132350884","copies":4}'  # 200
curl -i -X DELETE http://localhost:8080/books/2                        # 204
curl -i -X DELETE http://localhost:8080/books/999                      # 404
```

Each status code must match the comment.

- [ ] **Step 3: Commit and tag**

```bash
git add code/unit1-library-inmem
git commit -m "feat(unit1): lab 04 — full Book CRUD with status codes"
git tag lab-04-end
```

---

### Task 6: Lab 5 reference code — Query-parameter search

**Goal:** Add `GET /books/search?title=&author=` filtering in-memory.

**Files:**
- Modify: `code/unit1-library-inmem/src/main/java/com/example/library/book/BookController.java`

- [ ] **Step 1: Add `/search` endpoint to `BookController`**

Insert this method into `BookController` (above `findById`):

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

- [ ] **Step 2: Verify**

```bash
./mvnw spring-boot:run
curl 'http://localhost:8080/books/search?title=clean'
curl 'http://localhost:8080/books/search?author=bloch'
curl 'http://localhost:8080/books/search?title=java&author=bloch'
```

Each should return only matching books.

- [ ] **Step 3: Commit and tag**

```bash
git add code/unit1-library-inmem
git commit -m "feat(unit1): lab 05 — query-param book search"
git tag lab-05-end
```

---

### Task 7: Lab 6 reference code — Member CRUD (consolidation)

**Goal:** Add `Member` resource mirroring the `Book` pattern.

**Files:**
- Create: `code/unit1-library-inmem/src/main/java/com/example/library/member/Member.java`
- Create: `code/unit1-library-inmem/src/main/java/com/example/library/member/MemberController.java`

- [ ] **Step 1: Create `Member` POJO**

```java
package com.example.library.member;

import java.time.LocalDate;

public class Member {
    private Long id;
    private String name;
    private String email;
    private LocalDate registeredOn;

    public Member() {}

    public Member(Long id, String name, String email, LocalDate registeredOn) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.registeredOn = registeredOn;
    }

    // Standard getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(LocalDate registeredOn) { this.registeredOn = registeredOn; }
}
```

- [ ] **Step 2: Create `MemberController` (mirror of `BookController` pattern)**

```java
package com.example.library.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final List<Member> members = new ArrayList<>(List.of(
            new Member(1L, "Asha Rao", "asha@example.com", LocalDate.of(2024, 1, 10)),
            new Member(2L, "Vikram Singh", "vikram@example.com", LocalDate.of(2024, 3, 22))
    ));
    private final AtomicLong idGenerator = new AtomicLong(3);

    @GetMapping
    public List<Member> list() { return members; }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getById(@PathVariable Long id) {
        return findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Member> create(@RequestBody Member member) {
        member.setId(idGenerator.getAndIncrement());
        if (member.getRegisteredOn() == null) member.setRegisteredOn(LocalDate.now());
        members.add(member);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).body(member);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> update(@PathVariable Long id, @RequestBody Member updated) {
        Optional<Member> existing = findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        Member m = existing.get();
        m.setName(updated.getName());
        m.setEmail(updated.getEmail());
        return ResponseEntity.ok(m);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = members.removeIf(m -> m.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Optional<Member> findById(Long id) {
        return members.stream().filter(m -> m.getId().equals(id)).findFirst();
    }
}
```

- [ ] **Step 3: Verify**

```bash
./mvnw spring-boot:run
curl http://localhost:8080/members
curl -X POST http://localhost:8080/members \
    -H 'Content-Type: application/json' \
    -d '{"name":"Priya Sharma","email":"priya@example.com"}'
```

- [ ] **Step 4: Commit and tag**

```bash
git add code/unit1-library-inmem
git commit -m "feat(unit1): lab 06 — Member CRUD consolidation"
git tag lab-06-end
```

---

## Phase C — Reference Code: Unit 2 (Production-shape Library)

> Start a **fresh project** at `code/unit2-library-jpa/`. Begin with the same baseline as Unit 1 end-state (copy and rename, or regenerate). The Unit 2 series adds JPA, layers, validation, errors, tests, security, and Docker.

### Task 8: Lab 7 reference code — JPA refactor

**Goal:** Convert `Book` to a JPA entity backed by H2.

**Files:**
- Create: `code/unit2-library-jpa/pom.xml`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/LibraryApplication.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/book/Book.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/book/BookRepository.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/book/BookController.java`
- Create: `code/unit2-library-jpa/src/main/resources/application.properties`
- Create: `code/unit2-library-jpa/src/main/resources/data.sql`

- [ ] **Step 1: Generate baseline via `start.spring.io`**

Run (from `/Users/naveen/dev/workspaces/workshops/spring-boot/code/`):

```bash
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.3.5 \
  -d baseDir=unit2-library-jpa \
  -d groupId=com.example \
  -d artifactId=library \
  -d name=library \
  -d packageName=com.example.library \
  -d javaVersion=21 \
  -d dependencies=web,data-jpa,h2,validation \
  -o unit2.zip && unzip unit2.zip -d . && rm unit2.zip
```

- [ ] **Step 2: Delete the generated `LibraryApplication` test file scaffold if present and create `Book.java` as a JPA entity under `com/example/library/book/`**

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

    // Standard getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }
}
```

- [ ] **Step 3: Create `BookRepository`**

```java
package com.example.library.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);
}
```

- [ ] **Step 4: Replace `BookController` to use the repository directly (service layer comes in Lab 8)**

```java
package com.example.library.book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
        return repository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        Book saved = repository.save(book);
        return ResponseEntity.created(URI.create("/books/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody Book updated) {
        Optional<Book> existing = repository.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        Book b = existing.get();
        b.setTitle(updated.getTitle());
        b.setAuthor(updated.getAuthor());
        b.setIsbn(updated.getIsbn());
        b.setCopies(updated.getCopies());
        return ResponseEntity.ok(repository.save(b));
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

- [ ] **Step 5: Configure H2 and JPA in `application.properties`**

```properties
spring.datasource.url=jdbc:h2:mem:librarydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

- [ ] **Step 6: Seed data via `data.sql`**

```sql
INSERT INTO books (title, author, isbn, copies) VALUES ('Clean Code', 'Robert C. Martin', '9780132350884', 3);
INSERT INTO books (title, author, isbn, copies) VALUES ('Effective Java', 'Joshua Bloch', '9780134685991', 2);
INSERT INTO books (title, author, isbn, copies) VALUES ('The Pragmatic Programmer', 'Andrew Hunt', '9780201616224', 5);
```

- [ ] **Step 7: Verify**

```bash
./mvnw spring-boot:run
curl http://localhost:8080/books
```

Open `http://localhost:8080/h2-console` in browser. JDBC URL: `jdbc:h2:mem:librarydb`, user `sa`, no password. Run `SELECT * FROM books;`.

- [ ] **Step 8: Commit and tag**

```bash
git add code/unit2-library-jpa
git commit -m "feat(unit2): lab 07 — JPA refactor with H2"
git tag lab-07-end
```

---

### Task 9: Lab 8 reference code — Service layer, DTOs, validation, error handling

**Goal:** Insert a service layer, introduce a DTO, validate input, return structured errors via `@ControllerAdvice`.

**Files:**
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/book/BookService.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/book/BookDTO.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/book/BookMapper.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/common/BookNotFoundException.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/common/GlobalExceptionHandler.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/common/ErrorResponse.java`
- Modify: `code/unit2-library-jpa/src/main/java/com/example/library/book/BookController.java`

- [ ] **Step 1: `BookDTO` with validation annotations**

```java
package com.example.library.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    public BookDTO() {}

    // Standard getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }
}
```

- [ ] **Step 2: `BookMapper`**

```java
package com.example.library.book;

import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDTO toDto(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setCopies(book.getCopies());
        return dto;
    }

    public Book toEntity(BookDTO dto) {
        Book b = new Book(dto.getTitle(), dto.getAuthor(), dto.getIsbn(), dto.getCopies());
        b.setId(dto.getId());
        return b;
    }
}
```

- [ ] **Step 3: `BookNotFoundException`**

```java
package com.example.library.common;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Book not found: " + id);
    }
}
```

- [ ] **Step 4: `ErrorResponse`**

```java
package com.example.library.common;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Instant timestamp,
        List<Map<String, String>> fieldErrors
) {}
```

- [ ] **Step 5: `GlobalExceptionHandler`**

```java
package com.example.library.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(BookNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(404, ex.getMessage(), Instant.now(), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList();
        ErrorResponse body = new ErrorResponse(400, "Validation failed", Instant.now(), fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }
}
```

- [ ] **Step 6: `BookService`**

```java
package com.example.library.book;

import com.example.library.common.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;
    private final BookMapper mapper;

    public BookService(BookRepository repository, BookMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<BookDTO> listAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public BookDTO get(Long id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow(() -> new BookNotFoundException(id));
    }

    public BookDTO create(BookDTO dto) {
        Book saved = repository.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    public BookDTO update(Long id, BookDTO dto) {
        Book existing = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        existing.setTitle(dto.getTitle());
        existing.setAuthor(dto.getAuthor());
        existing.setIsbn(dto.getIsbn());
        existing.setCopies(dto.getCopies());
        return mapper.toDto(repository.save(existing));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new BookNotFoundException(id);
        repository.deleteById(id);
    }

    public List<BookDTO> search(String title, String author) {
        return repository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author)
                .stream().map(mapper::toDto).toList();
    }
}
```

- [ ] **Step 7: Replace `BookController` to delegate to the service**

```java
package com.example.library.book;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookDTO> list() { return service.listAll(); }

    @GetMapping("/{id}")
    public BookDTO getById(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    public ResponseEntity<BookDTO> create(@Valid @RequestBody BookDTO dto) {
        BookDTO saved = service.create(dto);
        return ResponseEntity.created(URI.create("/books/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public BookDTO update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<BookDTO> search(@RequestParam(defaultValue = "") String title,
                                @RequestParam(defaultValue = "") String author) {
        return service.search(title, author);
    }
}
```

- [ ] **Step 8: Verify**

```bash
./mvnw spring-boot:run
curl -i http://localhost:8080/books/999            # 404 with structured JSON
curl -i -X POST http://localhost:8080/books \
    -H 'Content-Type: application/json' \
    -d '{"title":"","author":"X"}'                 # 400 with fieldErrors array
```

The 404 response body must include `status`, `message`, `timestamp`. The 400 must include a non-empty `fieldErrors` array.

- [ ] **Step 9: Commit and tag**

```bash
git add code/unit2-library-jpa
git commit -m "feat(unit2): lab 08 — service layer, DTOs, validation, error handling"
git tag lab-08-end
```

---

### Task 10: Lab 9 reference code — Member and Loan with relationships

**Goal:** Add `Member` and `Loan` entities; expose borrow/return endpoints; decrement/increment available copies.

**Files:**
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/member/Member.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/member/MemberRepository.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/member/MemberService.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/member/MemberDTO.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/member/MemberController.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/loan/Loan.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/loan/LoanRepository.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/loan/LoanService.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/loan/LoanController.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/common/MemberNotFoundException.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/common/LoanNotFoundException.java`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/common/BorrowNotAllowedException.java`
- Modify: `code/unit2-library-jpa/src/main/java/com/example/library/common/GlobalExceptionHandler.java`

- [ ] **Step 1: `Member` entity**

```java
package com.example.library.member;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "members")
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private LocalDate registeredOn;

    public Member() {}

    public Member(String name, String email, LocalDate registeredOn) {
        this.name = name;
        this.email = email;
        this.registeredOn = registeredOn;
    }

    // Standard getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(LocalDate registeredOn) { this.registeredOn = registeredOn; }
}
```

- [ ] **Step 2: `Loan` entity with relationships**

```java
package com.example.library.loan;

import com.example.library.book.Book;
import com.example.library.member.Member;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate borrowedOn;

    private LocalDate returnedOn;

    public Loan() {}

    public Loan(Book book, Member member, LocalDate borrowedOn) {
        this.book = book;
        this.member = member;
        this.borrowedOn = borrowedOn;
    }

    public Long getId() { return id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public LocalDate getBorrowedOn() { return borrowedOn; }
    public void setBorrowedOn(LocalDate borrowedOn) { this.borrowedOn = borrowedOn; }
    public LocalDate getReturnedOn() { return returnedOn; }
    public void setReturnedOn(LocalDate returnedOn) { this.returnedOn = returnedOn; }
}
```

- [ ] **Step 3: Repositories**

`MemberRepository.java`:

```java
package com.example.library.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {}
```

`LoanRepository.java`:

```java
package com.example.library.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMemberIdAndReturnedOnIsNull(Long memberId);
}
```

- [ ] **Step 4: `MemberDTO`**

```java
package com.example.library.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class MemberDTO {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank @Email
    private String email;

    private LocalDate registeredOn;

    // Standard getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(LocalDate registeredOn) { this.registeredOn = registeredOn; }
}
```

- [ ] **Step 5: `MemberService` and `MemberController`**

`MemberService.java`:

```java
package com.example.library.member;

import com.example.library.common.MemberNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<MemberDTO> listAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public MemberDTO get(Long id) {
        return repository.findById(id).map(this::toDto).orElseThrow(() -> new MemberNotFoundException(id));
    }

    public MemberDTO create(MemberDTO dto) {
        Member m = new Member(dto.getName(), dto.getEmail(),
                dto.getRegisteredOn() != null ? dto.getRegisteredOn() : LocalDate.now());
        return toDto(repository.save(m));
    }

    private MemberDTO toDto(Member m) {
        MemberDTO dto = new MemberDTO();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setEmail(m.getEmail());
        dto.setRegisteredOn(m.getRegisteredOn());
        return dto;
    }
}
```

`MemberController.java`:

```java
package com.example.library.member;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) { this.service = service; }

    @GetMapping
    public List<MemberDTO> list() { return service.listAll(); }

    @GetMapping("/{id}")
    public MemberDTO getById(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    public ResponseEntity<MemberDTO> create(@Valid @RequestBody MemberDTO dto) {
        MemberDTO saved = service.create(dto);
        return ResponseEntity.created(URI.create("/members/" + saved.getId())).body(saved);
    }
}
```

- [ ] **Step 6: `LoanService` with borrow/return logic**

```java
package com.example.library.loan;

import com.example.library.book.Book;
import com.example.library.book.BookRepository;
import com.example.library.common.BookNotFoundException;
import com.example.library.common.BorrowNotAllowedException;
import com.example.library.common.LoanNotFoundException;
import com.example.library.common.MemberNotFoundException;
import com.example.library.member.Member;
import com.example.library.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class LoanService {

    private final LoanRepository loans;
    private final BookRepository books;
    private final MemberRepository members;

    public LoanService(LoanRepository loans, BookRepository books, MemberRepository members) {
        this.loans = loans;
        this.books = books;
        this.members = members;
    }

    @Transactional
    public Loan borrow(Long bookId, Long memberId) {
        Book book = books.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        Member member = members.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        if (book.getCopies() <= 0) {
            throw new BorrowNotAllowedException("No copies available for book " + bookId);
        }
        book.setCopies(book.getCopies() - 1);
        books.save(book);
        return loans.save(new Loan(book, member, LocalDate.now()));
    }

    @Transactional
    public Loan returnLoan(Long loanId) {
        Loan loan = loans.findById(loanId).orElseThrow(() -> new LoanNotFoundException(loanId));
        if (loan.getReturnedOn() != null) {
            throw new BorrowNotAllowedException("Loan " + loanId + " already returned");
        }
        loan.setReturnedOn(LocalDate.now());
        Book book = loan.getBook();
        book.setCopies(book.getCopies() + 1);
        books.save(book);
        return loans.save(loan);
    }
}
```

- [ ] **Step 7: `LoanController`**

```java
package com.example.library.loan;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Loan> borrow(@RequestParam Long bookId, @RequestParam Long memberId) {
        Loan loan = service.borrow(bookId, memberId);
        return ResponseEntity.created(URI.create("/loans/" + loan.getId())).body(loan);
    }

    @PutMapping("/{id}/return")
    public Loan returnLoan(@PathVariable Long id) {
        return service.returnLoan(id);
    }
}
```

- [ ] **Step 8: New exceptions**

`MemberNotFoundException.java`:

```java
package com.example.library.common;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long id) { super("Member not found: " + id); }
}
```

`LoanNotFoundException.java`:

```java
package com.example.library.common;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Long id) { super("Loan not found: " + id); }
}
```

`BorrowNotAllowedException.java`:

```java
package com.example.library.common;

public class BorrowNotAllowedException extends RuntimeException {
    public BorrowNotAllowedException(String message) { super(message); }
}
```

- [ ] **Step 9: Extend `GlobalExceptionHandler`**

Add these methods to the existing `GlobalExceptionHandler`:

```java
@ExceptionHandler({MemberNotFoundException.class, LoanNotFoundException.class})
public ResponseEntity<ErrorResponse> handleOtherNotFound(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage(), Instant.now(), List.of()));
}

@ExceptionHandler(BorrowNotAllowedException.class)
public ResponseEntity<ErrorResponse> handleBorrowNotAllowed(BorrowNotAllowedException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(409, ex.getMessage(), Instant.now(), List.of()));
}
```

- [ ] **Step 10: Verify**

```bash
./mvnw spring-boot:run
# Create a member
curl -i -X POST http://localhost:8080/members -H 'Content-Type: application/json' \
    -d '{"name":"Asha","email":"asha@example.com"}'
# Borrow book 1 with member 1
curl -i -X POST 'http://localhost:8080/loans?bookId=1&memberId=1'
# Return it
curl -i -X PUT http://localhost:8080/loans/1/return
```

Verify book 1's `copies` drops by one on borrow, restores on return.

- [ ] **Step 11: Commit and tag**

```bash
git add code/unit2-library-jpa
git commit -m "feat(unit2): lab 09 — Member, Loan, relationships, borrow/return"
git tag lab-09-end
```

---

### Task 11: Lab 10 reference code — Testing

**Goal:** One `@DataJpaTest` and one `@WebMvcTest` with at least four cases (incl. 404 and validation 400).

**Files:**
- Create: `code/unit2-library-jpa/src/test/java/com/example/library/book/BookRepositoryTest.java`
- Create: `code/unit2-library-jpa/src/test/java/com/example/library/book/BookControllerTest.java`

- [ ] **Step 1: `BookRepositoryTest`**

```java
package com.example.library.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Test
    void findByTitleAndAuthor_returnsMatches() {
        repository.save(new Book("Clean Code", "Robert C. Martin", "isbn-1", 1));
        repository.save(new Book("Clean Architecture", "Robert C. Martin", "isbn-2", 1));
        repository.save(new Book("Effective Java", "Joshua Bloch", "isbn-3", 1));

        List<Book> result = repository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("clean", "martin");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
    }
}
```

- [ ] **Step 2: `BookControllerTest`**

```java
package com.example.library.book;

import com.example.library.common.BookNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;

    @MockBean private BookService service;

    @Test
    void list_returnsAllBooks() throws Exception {
        BookDTO dto = new BookDTO();
        dto.setId(1L); dto.setTitle("Clean Code"); dto.setAuthor("RCM"); dto.setIsbn("9780132350884"); dto.setCopies(1);
        when(service.listAll()).thenReturn(List.of(dto));

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void getById_returns200WhenFound() throws Exception {
        BookDTO dto = new BookDTO();
        dto.setId(1L); dto.setTitle("Clean Code"); dto.setAuthor("RCM"); dto.setIsbn("9780132350884"); dto.setCopies(1);
        when(service.get(1L)).thenReturn(dto);

        mvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void getById_returns404WhenMissing() throws Exception {
        when(service.get(eq(999L))).thenThrow(new BookNotFoundException(999L));

        mvc.perform(get("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void create_returns400WhenValidationFails() throws Exception {
        BookDTO invalid = new BookDTO();
        invalid.setTitle("");          // blank — violates @NotBlank
        invalid.setAuthor("Someone");
        invalid.setIsbn("9780132350884");
        invalid.setCopies(1);

        mvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));
    }
}
```

- [ ] **Step 3: Run the tests**

```bash
./mvnw test
```

Expected: all four `BookControllerTest` cases plus the one `BookRepositoryTest` pass.

- [ ] **Step 4: Commit and tag**

```bash
git add code/unit2-library-jpa
git commit -m "test(unit2): lab 10 — @DataJpaTest + @WebMvcTest"
git tag lab-10-end
```

---

### Task 12: Lab 11 reference code — Profiles, security, ops

**Goal:** Add `prod` profile (MySQL), enable Actuator, protect `/loans/**` with HTTP Basic.

**Files:**
- Modify: `code/unit2-library-jpa/pom.xml`
- Create: `code/unit2-library-jpa/src/main/resources/application-prod.properties`
- Create: `code/unit2-library-jpa/src/main/java/com/example/library/security/SecurityConfig.java`
- Modify: `code/unit2-library-jpa/src/main/resources/application.properties`

- [ ] **Step 1: Add Maven dependencies**

Add to `pom.xml` `<dependencies>` section:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

- [ ] **Step 2: Enable actuator endpoints in `application.properties`**

Append:

```properties
management.endpoints.web.exposure.include=health,info
management.info.env.enabled=true
info.app.name=Library Management
info.app.version=1.0.0

spring.security.user.name=admin
spring.security.user.password=admin
```

- [ ] **Step 3: `application-prod.properties`**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/librarydb
spring.datasource.username=library_user
spring.datasource.password=library_pass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

spring.h2.console.enabled=false
```

- [ ] **Step 4: `SecurityConfig`**

```java
package com.example.library.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/loans/**").authenticated()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(b -> {})
                .headers(h -> h.frameOptions(f -> f.sameOrigin())); // allow H2 console
        return http.build();
    }
}
```

- [ ] **Step 5: Verify (dev profile)**

```bash
./mvnw spring-boot:run
curl http://localhost:8080/books                          # 200, public
curl -i 'http://localhost:8080/loans?bookId=1&memberId=1' -X POST   # 401 without auth
curl -u admin:admin -i -X POST 'http://localhost:8080/loans?bookId=1&memberId=1'   # 201
curl http://localhost:8080/actuator/health                # {"status":"UP"}
curl http://localhost:8080/actuator/info                  # app metadata
```

- [ ] **Step 6: Verify prod profile boots (MySQL connection may fail; that's expected if MySQL isn't running — just confirm the profile is loaded)**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod 2>&1 | grep -E "(profile|MySQL|HikariPool)" | head -5
```

Expected: log line "The following 1 profile is active: 'prod'". A connection error is acceptable — the point is the profile loads.

- [ ] **Step 7: Update existing tests for security**

In `BookControllerTest`, the security filter chain now permits `/books`, so no changes needed. But `@WebMvcTest` may require `spring-security-test` for some scenarios. Verify tests still pass:

```bash
./mvnw test
```

If any test fails due to security, annotate the test class with `@AutoConfigureMockMvc(addFilters = false)`.

- [ ] **Step 8: Commit and tag**

```bash
git add code/unit2-library-jpa
git commit -m "feat(unit2): lab 11 — prod profile, actuator, HTTP Basic security"
git tag lab-11-end
```

---

### Task 13: Lab 12 reference code — Package and Dockerize

**Goal:** Package as jar, write `Dockerfile`, build and run the container.

**Files:**
- Create: `code/unit2-library-jpa/Dockerfile`
- Create: `code/unit2-library-jpa/.dockerignore`

- [ ] **Step 1: `Dockerfile` (multi-stage)**

```dockerfile
# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

- [ ] **Step 2: `.dockerignore`**

```
target/
.git/
.idea/
*.iml
.DS_Store
```

- [ ] **Step 3: Build and run the container**

```bash
cd code/unit2-library-jpa
docker build -t library-app:1.0 .
docker run --rm -p 8080:8080 --name library-app library-app:1.0
```

In another terminal:

```bash
curl http://localhost:8080/books
curl http://localhost:8080/actuator/health
```

- [ ] **Step 4: Stop the container and commit**

```bash
docker stop library-app
git add code/unit2-library-jpa
git commit -m "feat(unit2): lab 12 — Dockerfile and container build"
git tag lab-12-end
```

---

## Phase D — Lab Handouts

> Each handout follows the same structure: **Objective · Prerequisites · Starter state (git tag) · Step-by-step instructions · Expected output · Stretch task · Common pitfalls.** Write each lab as a single markdown file. The "starter state" references the previous lab's `lab-NN-end` tag.

### Task 14: Lab 1 handout — Hello, Spring

**Files:**
- Create: `labs/lab-01-hello-spring.md`

- [ ] **Step 1: Write the handout**

```markdown
# Lab 1 — Hello, Spring Boot

**Duration:** ~75 minutes
**Day:** 1, Slot 2

## Objective

Generate a Spring Boot project from scratch, run it, and expose a single `GET /hello` endpoint.

## Prerequisites

- JDK 21, IntelliJ Community, Git installed (see [`../README.md`](../README.md)).

## Starter state

None — you create the project from scratch.

## Steps

1. Open https://start.spring.io in a browser.
2. Configure: Project = Maven, Language = Java, Spring Boot = 3.3.x, Group = `com.example`, Artifact = `library`, Java = 21.
3. Add dependency: **Spring Web**.
4. Click **Generate**, download the zip, unzip into your workspace.
5. Open the project in IntelliJ. Wait for Maven to import.
6. Find `LibraryApplication.java`. Right-click → **Run**.
7. Watch the console — wait for the line "Started LibraryApplication in N seconds".
8. Open `http://localhost:8080` in a browser. You should see a Whitelabel error page (no endpoint at `/`).
9. Create a new class `HelloController` next to `LibraryApplication`:

   ```java
   @RestController
   public class HelloController {
       @GetMapping("/hello")
       public String hello() {
           return "Hello, Spring Boot!";
       }
   }
   ```

10. Stop the app. Re-run it.
11. Open a terminal and run: `curl http://localhost:8080/hello`

## Expected output

```
$ curl http://localhost:8080/hello
Hello, Spring Boot!
```

## Stretch task

- Add a second endpoint `GET /hello/{name}` that returns `"Hello, <name>!"`.

## Common pitfalls

- **Port 8080 already in use** — set `server.port=8081` in `src/main/resources/application.properties`.
- **Imports missing** — IntelliJ may not auto-import `@RestController` and `@GetMapping`. Press Alt+Enter on the red text.
- **Wrong Java version** — check Project Structure → Project SDK is set to 21.

## Checkpoint

Reference solution: `git checkout lab-01-end` inside `code/unit1-library-inmem/`.
```

- [ ] **Step 2: Commit**

```bash
git add labs/lab-01-hello-spring.md
git commit -m "docs: lab 01 handout"
```

---

### Task 15: Lab 2 handout — Beans and configuration

**Files:**
- Create: `labs/lab-02-beans-and-config.md`

- [ ] **Step 1: Write the handout**

```markdown
# Lab 2 — Beans and Configuration

**Duration:** ~75 minutes
**Day:** 1, Slot 4

## Objective

Inject a service into a controller using constructor injection, read configuration from `application.properties`, and switch between two implementations using `@Profile`.

## Prerequisites

- Lab 1 complete.

## Starter state

`git checkout lab-01-end` (or your own Lab 1 result).

## Steps

1. Create package `com.example.library.greeting`.
2. Inside it, create an interface `GreetingService`:

   ```java
   public interface GreetingService {
       String greet(String name);
   }
   ```

3. Create `EnglishGreetingService` annotated with `@Service` and `@Profile({"default", "en"})`. Inject a `@Value("${greeting.prefix:Hello}") String prefix` via the constructor. `greet(name)` returns `prefix + ", " + name + "!"`.
4. Create `HindiGreetingService` annotated with `@Service` and `@Profile("hi")`. `greet(name)` returns `"Namaste, " + name + "!"`.
5. Modify `HelloController` to take a `GreetingService` via constructor injection. Replace the `/hello` endpoint with `GET /hello/{name}` calling `greetingService.greet(name)`.
6. Add to `application.properties`:

   ```properties
   greeting.prefix=Hello
   ```

7. Run the app (default profile). Test:

   ```bash
   curl http://localhost:8080/hello/Asha
   # → Hello, Asha!
   ```

8. Stop the app. Run with `--spring.profiles.active=hi` (in IntelliJ: Edit Configurations → Active profiles = `hi`).

   ```bash
   curl http://localhost:8080/hello/Asha
   # → Namaste, Asha!
   ```

## Expected output

Default profile returns "Hello, Asha!". `hi` profile returns "Namaste, Asha!".

## Stretch task

- Add a `@ConfigurationProperties` class to bind `greeting.*` properties (instead of `@Value`).
- Add a `TamilGreetingService` under `@Profile("ta")` returning "Vanakkam, ...!".

## Common pitfalls

- **NoUniqueBeanDefinitionException** — only one `GreetingService` should match the active profile. Make sure `EnglishGreetingService` is only `@Profile({"default", "en"})`, not unannotated.
- **Property not resolved** — `@Value("${greeting.prefix}")` without a default crashes if the property is missing. The `${greeting.prefix:Hello}` syntax provides a fallback.

## Checkpoint

Reference solution: `git checkout lab-02-end` inside `code/unit1-library-inmem/`.
```

- [ ] **Step 2: Commit**

```bash
git add labs/lab-02-beans-and-config.md
git commit -m "docs: lab 02 handout"
```

---

### Task 16: Lab 3 handout — First resource

**Files:**
- Create: `labs/lab-03-first-resource.md`

- [ ] **Step 1: Write the handout** (follow the same structure as Labs 1 and 2)

Content covers:
- Creating `Book` POJO with fields `id`, `title`, `author`, `isbn`, `copies` + getters/setters.
- Creating `BookController` annotated with `@RestController` and `@RequestMapping("/books")`.
- Initializing an `ArrayList<Book>` field with three seed books.
- A single `@GetMapping` method returning the list.
- Expected output: `curl http://localhost:8080/books` returns a JSON array.
- Stretch task: add `Pageable` and return a `Page<Book>` slice.
- Common pitfall: Jackson can't serialize the POJO if getters are missing → add Lombok or write them explicitly.

Use the same structure as Lab 2's handout. Reference `lab-03-end` as the checkpoint.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-03-first-resource.md
git commit -m "docs: lab 03 handout"
```

---

### Task 17: Lab 4 handout — Full CRUD

**Files:**
- Create: `labs/lab-04-full-crud.md`

- [ ] **Step 1: Write the handout**

Cover:
- Extending `BookController` to add `GET /books/{id}`, `POST /books`, `PUT /books/{id}`, `DELETE /books/{id}`.
- Correct status codes: 200, 201 (with Location header), 204, 404.
- Use `ResponseEntity<...>`, `@PathVariable`, `@RequestBody`.
- Use `AtomicLong` for ID generation.
- Test each verb with curl (give the full curl commands).
- Stretch task: return 409 Conflict when POST tries to insert a duplicate ISBN.
- Common pitfall: forgetting `@RequestBody` on POST/PUT → Spring will silently bind nothing.

Checkpoint: `lab-04-end`.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-04-full-crud.md
git commit -m "docs: lab 04 handout"
```

---

### Task 18: Lab 5 handout — Query-param search

**Files:**
- Create: `labs/lab-05-query-params.md`

- [ ] **Step 1: Write the handout**

Cover:
- Adding `GET /books/search?title=&author=` with `@RequestParam(required = false)`.
- Filtering the in-memory list with a stream.
- Curl examples: search by title, by author, by both.
- Stretch task: extract the filter logic into a `BookSearchService` so Lab 8's refactor has even more to do.
- Common pitfall: omitting `(required = false)` makes the parameter mandatory → 400.

Checkpoint: `lab-05-end`.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-05-query-params.md
git commit -m "docs: lab 05 handout"
```

---

### Task 19: Lab 6 handout — Member CRUD (solo consolidation)

**Files:**
- Create: `labs/lab-06-solo-consolidation.md`

- [ ] **Step 1: Write the handout — deliberately less hand-holding**

Cover:
- Goal: build `Member` CRUD mirroring `BookController` mostly unaided.
- Provide the `Member` POJO field list (id, name, email, registeredOn) but NOT the controller code.
- Provide curl commands for the expected endpoints so students know the API shape.
- Stretch task: add `GET /members/search?email=` returning members with matching email.
- Common pitfall: forgetting `@RequestMapping("/members")` → endpoints collide with `/books`.

Checkpoint: `lab-06-end`.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-06-solo-consolidation.md
git commit -m "docs: lab 06 handout"
```

---

### Task 20: Lab 7 handout — JPA refactor

**Files:**
- Create: `labs/lab-07-jpa-refactor.md`

- [ ] **Step 1: Write the handout**

Cover:
- Goal: replace in-memory list with JPA + H2.
- Start a NEW project at `code/unit2-library-jpa/` from `start.spring.io` with dependencies: Web, Spring Data JPA, H2, Validation.
- Convert `Book` to `@Entity` with `@Id @GeneratedValue`.
- Create `BookRepository extends JpaRepository<Book, Long>` plus custom finder `findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase`.
- Update `BookController` to inject the repository and use it.
- Configure H2 in `application.properties` (URL, dialect, H2 console).
- Seed via `data.sql`.
- Verify the H2 console at `/h2-console`.
- Stretch task: switch H2 to file-mode (`jdbc:h2:file:./data/library`) and confirm data survives restart.
- Common pitfalls: forgetting `spring.jpa.hibernate.ddl-auto=update`, forgetting to enable `spring.h2.console.enabled=true`, ISBN unique constraint clash in `data.sql`.

Checkpoint: `lab-07-end`.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-07-jpa-refactor.md
git commit -m "docs: lab 07 handout"
```

---

### Task 21: Lab 8 handout — Layers, DTOs, validation, error handling

**Files:**
- Create: `labs/lab-08-layers-dtos-errors.md`

- [ ] **Step 1: Write the handout**

Cover:
- Goal: insert a `BookService`, separate `BookDTO` from entity, validate with `@Valid`, return structured errors via `@ControllerAdvice`.
- Create `BookDTO` (same fields) with `@NotBlank`, `@Size`, `@Min` annotations.
- Create `BookMapper` (manual `toDto` / `toEntity`).
- Create `BookService` that depends on `BookRepository` and `BookMapper`.
- Replace controller methods to call the service. Throw `BookNotFoundException` from service.
- Create `BookNotFoundException`, `ErrorResponse` (record), `GlobalExceptionHandler` with `@RestControllerAdvice`.
- Test: POST an invalid book → 400 with `fieldErrors`. GET `/books/999` → 404 with structured body.
- Stretch task: write a custom Bean Validation annotation `@ValidISBN` that checks the 10/13 digit pattern.
- Common pitfalls: forgetting `@Valid` on the `@RequestBody` parameter, mixing entity and DTO in service signatures, `@ControllerAdvice` instead of `@RestControllerAdvice` (the former needs `@ResponseBody` on each method).

Checkpoint: `lab-08-end`.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-08-layers-dtos-errors.md
git commit -m "docs: lab 08 handout"
```

---

### Task 22: Lab 9 handout — Relationships

**Files:**
- Create: `labs/lab-09-relationships.md`

- [ ] **Step 1: Write the handout**

Cover:
- Goal: add `Member` and `Loan` entities; expose borrow/return endpoints.
- Build `Member` entity with `MemberRepository`, `MemberService`, `MemberController` (mirror Book pattern from Lab 8).
- Build `Loan` entity with `@ManyToOne Book` and `@ManyToOne Member`, plus `borrowedOn` and nullable `returnedOn`.
- `LoanService.borrow(bookId, memberId)`: decrements `book.copies`, creates a `Loan`. Throw `BorrowNotAllowedException` if `copies == 0`.
- `LoanService.returnLoan(loanId)`: sets `returnedOn`, increments `book.copies`. Throw if already returned.
- `LoanController`: `POST /loans?bookId=&memberId=`, `PUT /loans/{id}/return`.
- Extend `GlobalExceptionHandler` for the new exceptions (return 404 / 409).
- Curl walkthrough: create member → borrow → confirm `copies` dropped → return → confirm `copies` restored.
- Stretch task: add cascading delete (`cascade = CascadeType.ALL, orphanRemoval = true`) on `Member.loans`.
- Common pitfalls: `@JsonIgnore` needed if you add bidirectional relationships (Member ↔ Loan) to avoid infinite JSON recursion; transactional boundary — `LoanService.borrow` must be `@Transactional` so book save and loan save commit together.

Checkpoint: `lab-09-end`.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-09-relationships.md
git commit -m "docs: lab 09 handout"
```

---

### Task 23: Lab 10 handout — Testing

**Files:**
- Create: `labs/lab-10-testing.md`

- [ ] **Step 1: Write the handout**

Cover:
- Goal: write `@DataJpaTest` for `BookRepository`, `@WebMvcTest` for `BookController` with ≥4 cases.
- Explain the testing pyramid: full `@SpringBootTest` is heavy; slice tests are fast.
- `@DataJpaTest` test: seed three books, call the custom finder, assert two results returned.
- `@WebMvcTest(BookController.class)`: use `@MockBean BookService`, write four tests — list 200, get-by-id 200, get-by-id 404 (mock throws `BookNotFoundException`), POST with empty title 400 (validates `fieldErrors`).
- Run: `./mvnw test` — all pass.
- Stretch task: add an integration test with Testcontainers and a real MySQL.
- Common pitfalls: missing `spring-boot-starter-test` (comes with starter parent, but check); `@MockBean` deprecated in Spring Boot 3.4+ — for 3.3.x it's still fine; ObjectMapper not auto-wired without `@AutoConfigureJsonTesters`.

Checkpoint: `lab-10-end`.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-10-testing.md
git commit -m "docs: lab 10 handout"
```

---

### Task 24: Lab 11 handout — Profiles, security, ops

**Files:**
- Create: `labs/lab-11-profiles-security-ops.md`

- [ ] **Step 1: Write the handout**

Cover:
- Goal: introduce `prod` profile (MySQL), enable Actuator, protect `/loans/**` with HTTP Basic.
- Add `spring-boot-starter-security`, `spring-boot-starter-actuator`, `mysql-connector-j` to `pom.xml`.
- Create `application-prod.properties` pointing at `jdbc:mysql://localhost:3306/librarydb`.
- Write `SecurityConfig` with `@Bean SecurityFilterChain` permitting `/books/**`, `/members/**`, `/h2-console/**`, requiring auth on `/loans/**`. CSRF disabled, stateless session.
- Default user from properties: `spring.security.user.name=admin`, `spring.security.user.password=admin`.
- Verify: `/books` works without auth; `/loans` returns 401 without credentials, 201 with `-u admin:admin`.
- Verify: `/actuator/health` returns `{"status":"UP"}`, `/actuator/info` returns app metadata.
- Run with `--spring.profiles.active=prod` — confirm the prod profile is loaded (MySQL connection may fail; that's expected without a running MySQL).
- Stretch task: swap HTTP Basic for JWT — use `io.jsonwebtoken:jjwt` and a `/login` endpoint.
- Common pitfalls: forgetting to disable CSRF for a non-browser API; H2 console blocked by Spring Security default `frameOptions` — need `frameOptions(sameOrigin)`; new `@WebMvcTest` may fail because Spring Security applies filters — add `@AutoConfigureMockMvc(addFilters = false)` to the test class or `.with(user("admin"))` to requests.

Checkpoint: `lab-11-end`.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-11-profiles-security-ops.md
git commit -m "docs: lab 11 handout"
```

---

### Task 25: Lab 12 handout — Capstone: package and ship

**Files:**
- Create: `labs/lab-12-capstone-package-ship.md`

- [ ] **Step 1: Write the handout**

Cover:
- Goal: build the app as a jar, write a `Dockerfile`, build and run the container.
- Run `./mvnw clean package` — outputs `target/library-0.0.1-SNAPSHOT.jar`.
- Run the jar directly: `java -jar target/library-0.0.1-SNAPSHOT.jar` and confirm it works.
- Write a `Dockerfile` (multi-stage) — the handout shows the full Dockerfile from Task 13 step 1.
- Build: `docker build -t library-app:1.0 .`
- Run: `docker run --rm -p 8080:8080 library-app:1.0`
- Verify endpoints work from the host: `curl http://localhost:8080/books`.
- **Submission:** push to a public GitHub repository, submit the URL. Repository must contain README explaining build/run, source code, tests passing, Dockerfile, sample curl commands.
- Stretch task: use the Spring Boot `bootBuildImage` Maven goal (Cloud Native Buildpacks) instead of a hand-written Dockerfile.
- Common pitfalls: `target/*.jar` not built before `docker build` — multi-stage Dockerfile handles this; forgetting `EXPOSE 8080`; running as root inside the container (acceptable for the seminar — point out the production concern).

Checkpoint: `lab-12-end`.

## Evaluation criteria

See [`../evaluation.md`](../evaluation.md) for the capstone rubric.

- [ ] **Step 2: Commit**

```bash
git add labs/lab-12-capstone-package-ship.md
git commit -m "docs: lab 12 capstone handout"
```

---

## Phase E — Lecture Decks

> Each deck is a markdown file in `slides/`. Format: title slide, then 8–15 content slides. Use `---` between slides (compatible with reveal.js, marp, or just readable as markdown). No interactive elements — these are reference decks the instructor can convert to PDF or run live.

### Task 26: Day 1 Lecture 1 deck — Spring fundamentals

**Files:**
- Create: `slides/day1-lecture1-spring-fundamentals.md`

- [ ] **Step 1: Write the deck**

```markdown
---
title: Day 1 · Lecture 1 — Spring fundamentals
duration: 90 min
---

# Day 1 · Lecture 1
## Spring Boot, what and why

---

## The problem Spring solves

- Java enterprise apps before Spring: lots of XML, lots of "plumbing" code, lots of `new` everywhere.
- Wiring up a service that needs a logger, a database, and a config file took 50 lines of glue.
- Testing was hard because everything was tightly coupled.

---

## Inversion of Control (IoC)

- "Don't call us, we'll call you."
- Instead of: `Service s = new Service(new Repo(new Db("url")));`
- You declare: "I need a `Service`." Spring builds it for you.

---

## Dependency Injection by hand

```java
class BookController {
    private final BookService service;

    BookController(BookService service) {   // <-- constructor injection
        this.service = service;
    }
}
```

You never write `new BookService()`. Spring does, once, and hands you the same instance.

---

## What Spring Boot adds on top of Spring

- Auto-configuration — "if you've got `spring-boot-starter-web` on the classpath, you probably want an embedded Tomcat. Done."
- Starters — single dependency pulls in 12 related libraries.
- Embedded server — no WAR file, no Tomcat install. Just `java -jar`.
- Sensible defaults.

---

## What is `@SpringBootApplication`?

It's three annotations in one:
- `@Configuration` — this class can define beans.
- `@EnableAutoConfiguration` — turn on auto-config magic.
- `@ComponentScan` — scan this package and subpackages for `@Component`, `@Service`, `@Controller`.

---

## Maven in 90 seconds

- `pom.xml` = your project's recipe.
- `<parent>spring-boot-starter-parent</parent>` = inherit Spring Boot's version management.
- `<dependencies>` = libraries.
- `mvn package` = compile, test, build jar.
- `mvnw` (wrapper) ships with the project — students don't need to install Maven globally.

---

## Demo: what `start.spring.io` actually creates

- `pom.xml` — Maven build file.
- `src/main/java/com/example/library/LibraryApplication.java` — the entry point.
- `src/main/resources/application.properties` — config.
- `src/test/java/.../LibraryApplicationTests.java` — a smoke test.
- `mvnw` / `mvnw.cmd` — Maven wrapper scripts.

---

## What we're building this seminar

A **Library Management System** in two passes:

- Unit 1 (Days 1–2): in-memory, single-layer, just enough Spring.
- Unit 2 (Days 3–4): JPA, DB, layers, validation, testing, security, Docker.

By Day 4 you'll have a Docker container you can show off.

---

## Lab 1 preview

Generate a project, run "Hello, World".

If you can:

```
curl http://localhost:8080/hello
> Hello, Spring Boot!
```

You're done.

---

## Questions?
```

- [ ] **Step 2: Commit**

```bash
git add slides/day1-lecture1-spring-fundamentals.md
git commit -m "docs: day 1 lecture 1 slides"
```

---

### Task 27: Day 1 Lecture 2 deck — Beans and configuration

**Files:**
- Create: `slides/day1-lecture2-beans-and-config.md`

- [ ] **Step 1: Write the deck following the same structure**

Cover:
- What is a bean? Lifecycle (created by Spring, lives in the application context, destroyed at shutdown).
- `@Component` vs `@Service` vs `@Repository` vs `@Controller` — they're all `@Component` semantically; the others are aliases for readability and component scanning targeting.
- Constructor injection (preferred) vs `@Autowired` field injection (avoid).
- Why constructor injection wins: immutable, testable, fails fast at startup if a dependency is missing.
- `application.properties` — Spring's default config file. Key-value, plus YAML alternative.
- `@Value("${key}")` for one-off values; `@ConfigurationProperties` for grouped binding (mention only).
- Profiles: `@Profile("dev")` vs `@Profile("prod")`. Activate with `--spring.profiles.active=...`.
- Wrap with Lab 2 preview.

Length: 10–12 slides, follow the same markdown format with `---` between slides.

- [ ] **Step 2: Commit**

```bash
git add slides/day1-lecture2-beans-and-config.md
git commit -m "docs: day 1 lecture 2 slides"
```

---

### Task 28: Day 2 Lecture 3 deck — REST verbs and Spring MVC

**Files:**
- Create: `slides/day2-lecture3-rest-verbs.md`

- [ ] **Step 1: Write the deck**

Cover:
- HTTP method semantics: GET (read, idempotent), POST (create, not idempotent), PUT (full update, idempotent), PATCH (partial update), DELETE (remove, idempotent).
- Spring MVC annotations: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.
- `@PathVariable` vs `@RequestParam` vs `@RequestBody` — when to use which.
- Status codes that matter: 200 OK, 201 Created (with Location header), 204 No Content, 400 Bad Request, 404 Not Found, 409 Conflict, 500 Server Error.
- `ResponseEntity<T>` — explicit control over status and headers.
- Jackson does JSON serialization automatically (POJOs with getters → JSON).
- Content negotiation — JSON by default; you can ask for XML with `Accept: application/xml` + a dependency.
- Lab 4 preview.

Length: ~12 slides.

- [ ] **Step 2: Commit**

```bash
git add slides/day2-lecture3-rest-verbs.md
git commit -m "docs: day 2 lecture 3 slides"
```

---

### Task 29: Day 2 Lecture 4 deck — What's missing in a toy app

**Files:**
- Create: `slides/day2-lecture4-whats-missing.md`

- [ ] **Step 1: Write the deck**

Cover:
- The 4 gaps in everything we've built so far: no persistence, no validation, no error handling, no layers.
- Why these matter:
  - No persistence → restart loses data.
  - No validation → garbage in, garbage out.
  - No error handling → 500 Server Errors with stack traces leaking internals.
  - No layers → business logic in controllers, can't be reused or tested in isolation.
- Bridge to Unit 2.
- The architecture target: Controller → Service → Repository → Database.
- DTO vs Entity — why you separate them (entity = DB shape; DTO = API shape).
- Lab 5 + Lab 6 preview.

Length: ~8 slides.

- [ ] **Step 2: Commit**

```bash
git add slides/day2-lecture4-whats-missing.md
git commit -m "docs: day 2 lecture 4 slides"
```

---

### Task 30: Day 3 Lecture 5 deck — Layers and JPA

**Files:**
- Create: `slides/day3-lecture5-layers-and-jpa.md`

- [ ] **Step 1: Write the deck**

Cover:
- The layered architecture in detail with a diagram (controller / service / repository / DB).
- What JPA is (a spec), what Hibernate is (an implementation), what Spring Data JPA is (a productivity layer on top).
- `@Entity`, `@Id`, `@GeneratedValue`, `@Column`, `@Table`.
- `JpaRepository<Entity, IdType>` — what you get for free: `findAll`, `findById`, `save`, `delete`, `existsById`, etc.
- Derived query methods: `findByTitleContaining`, `findByEmailIgnoreCase`. The naming convention IS the query.
- `@Query` for when derived queries don't suffice (mention only).
- H2 as a learning database — in-memory, zero install, has a web console.
- `application.properties` config for H2 + JPA.
- `data.sql` for seed data.
- Lab 7 preview.

Length: ~14 slides.

- [ ] **Step 2: Commit**

```bash
git add slides/day3-lecture5-layers-and-jpa.md
git commit -m "docs: day 3 lecture 5 slides"
```

---

### Task 31: Day 3 Lecture 6 deck — Services, DTOs, validation

**Files:**
- Create: `slides/day3-lecture6-services-dtos-validation.md`

- [ ] **Step 1: Write the deck**

Cover:
- Service layer's job: business logic, transactions, orchestrating multiple repositories.
- `@Service` annotation — semantic alias for `@Component`.
- `@Transactional` — opens/commits/rolls back a DB transaction around the method. Why borrow-and-decrement-copies must be in one transaction.
- DTOs — why separate from entities. Avoids exposing DB-internal fields, lets the API evolve independently.
- A simple `BookMapper` — manual is fine for a seminar; MapStruct exists for real projects.
- Bean Validation (`jakarta.validation`): `@NotBlank`, `@NotNull`, `@Size`, `@Min`, `@Max`, `@Email`, `@Pattern`.
- `@Valid` on `@RequestBody` triggers validation; failures throw `MethodArgumentNotValidException`.
- `@ControllerAdvice` / `@RestControllerAdvice` — centralize error handling, return structured JSON.
- Lab 8 preview.

Length: ~12 slides.

- [ ] **Step 2: Commit**

```bash
git add slides/day3-lecture6-services-dtos-validation.md
git commit -m "docs: day 3 lecture 6 slides"
```

---

### Task 32: Day 4 Lecture 7 deck — Testing

**Files:**
- Create: `slides/day4-lecture7-testing.md`

- [ ] **Step 1: Write the deck**

Cover:
- The testing pyramid: lots of unit tests, fewer slice tests, even fewer full integration tests.
- `@SpringBootTest` — boots the entire app. Slow but realistic. Use sparingly.
- Slice tests:
  - `@WebMvcTest(SomeController.class)` — loads only the web layer, mocks the service.
  - `@DataJpaTest` — loads only JPA, uses an in-memory H2 by default.
  - `@JsonTest`, `@RestClientTest` — others (mention).
- `MockMvc` — fake HTTP requests without a real server.
- `@MockBean` — replaces a real bean with a Mockito mock (note: deprecated in 3.4+ but still standard in 3.3.x).
- AssertJ assertions: `assertThat(x).isEqualTo(y).hasSize(3).contains(...)`.
- Test naming: `methodName_condition_expectedResult`.
- TDD in passing: write the test first, watch it fail, write the minimum code to pass, refactor.
- Lab 10 preview.

Length: ~12 slides.

- [ ] **Step 2: Commit**

```bash
git add slides/day4-lecture7-testing.md
git commit -m "docs: day 4 lecture 7 slides"
```

---

### Task 33: Day 4 Lecture 8 deck — Profiles, security, ops, Docker

**Files:**
- Create: `slides/day4-lecture8-profiles-security-ops.md`

- [ ] **Step 1: Write the deck**

Cover:
- Profiles recap: how `application-{profile}.properties` overrides `application.properties`.
- Spring Boot Actuator: what it gives you (`/health`, `/info`, `/metrics`, `/env`, `/loggers`). Why you expose only some endpoints (security).
- Spring Security in 60 seconds:
  - `SecurityFilterChain` bean — declarative authorization rules.
  - HTTP Basic (simple, demo-grade); JWT (real-world, stateless); session cookies (browsers).
  - CSRF — when you need it (browser-based) and when to disable (stateless API).
- Packaging a Spring Boot app: `mvn package` produces a "fat jar" with everything inside. Run with `java -jar`.
- Docker in 10 minutes:
  - What an image is, what a container is.
  - Multi-stage Dockerfile — build in one stage (with JDK), run in another (with JRE only).
  - `docker build`, `docker run`, `docker logs`, `docker stop`.
- Where to go next: Spring Cloud, microservices, observability (Micrometer), reactive (WebFlux), Kafka, OAuth2.
- Lab 11 + Lab 12 preview.

Length: ~15 slides.

- [ ] **Step 2: Commit**

```bash
git add slides/day4-lecture8-profiles-security-ops.md
git commit -m "docs: day 4 lecture 8 slides"
```

---

## Phase F — Final Integration Documents

### Task 34: `curriculum.md`

**Files:**
- Create: `curriculum.md`

- [ ] **Step 1: Write the curriculum document**

Mirror the structure of Section 5 ("Day-by-Day Breakdown") from the design spec at `docs/superpowers/specs/2026-05-19-spring-boot-seminar-design.md`. For each day:

- Day header with theme.
- For each lecture: title, duration, talking points (3–5 bullets summarizing the key concepts from the corresponding slide deck).
- For each lab: title, duration, link to the handout in `labs/`.

Add a top-level "Daily rhythm" section reproducing the table from Section 4 of the spec.

Add a "How to use this document" section: instructor reads each day's section the night before; talking points are conversation starters, not lecture scripts; slide decks in `slides/` are the visual companion; lab handouts in `labs/` are what students follow.

- [ ] **Step 2: Commit**

```bash
git add curriculum.md
git commit -m "docs: day-by-day curriculum overview"
```

---

### Task 35: `evaluation.md`

**Files:**
- Create: `evaluation.md`

- [ ] **Step 1: Write the rubric**

```markdown
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
| Reasonable naming, no dead code, no console.print debugging | 4 |

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

For each completed stretch task across the 12 labs (visible in commits or README): +1 point, capped at 10.

## Grading bands

| Total | Grade |
|-------|-------|
| 90–100+ | A |
| 80–89 | B |
| 70–79 | C |
| 60–69 | D |
| <60 | Incomplete; rework required |
```

- [ ] **Step 2: Commit**

```bash
git add evaluation.md
git commit -m "docs: capstone evaluation rubric"
```

---

### Task 36: Finalize and verify

**Files:**
- Verify all deliverables in place.

- [ ] **Step 1: List all files and check structure**

```bash
cd /Users/naveen/dev/workspaces/workshops/spring-boot
find . -type f -not -path './.git/*' -not -path '*/target/*' -not -path '*/node_modules/*' | sort
```

Expected output includes:
- `README.md`, `curriculum.md`, `evaluation.md`
- `labs/lab-01-hello-spring.md` through `labs/lab-12-capstone-package-ship.md` (12 files)
- `slides/day1-lecture1-spring-fundamentals.md` through `slides/day4-lecture8-profiles-security-ops.md` (8 files)
- `code/unit1-library-inmem/` (Spring Boot project)
- `code/unit2-library-jpa/` (Spring Boot project)
- `docs/superpowers/specs/2026-05-19-spring-boot-seminar-design.md`
- `docs/superpowers/plans/2026-05-19-spring-boot-seminar.md`

- [ ] **Step 2: Verify git tags**

```bash
git tag --list 'lab-*'
```

Expected: `lab-01-end` through `lab-12-end` (12 tags).

- [ ] **Step 3: Verify both code projects build and tests pass**

```bash
cd code/unit1-library-inmem && ./mvnw -q -DskipTests package && cd ../..
cd code/unit2-library-jpa && ./mvnw -q test && cd ../..
```

Expected: both exit code 0.

- [ ] **Step 4: Verify Docker build for the capstone**

```bash
cd code/unit2-library-jpa
docker build -q -t library-app:1.0 .
```

Expected: prints an image ID, no errors.

- [ ] **Step 5: Final commit**

If any cleanup is needed, do it now.

```bash
git add -A
git commit -m "chore: final seminar kit ready" --allow-empty
```

---

## Self-review notes

- **Spec coverage:** All 12 labs from spec Section 6 mapped to Tasks 2–13 (code) and Tasks 14–25 (handouts). All 8 lectures from spec Section 5 mapped to Tasks 26–33. Top-level docs (README, curriculum, evaluation) covered by Tasks 1, 34, 35. Tooling assumptions (JDK 21, Spring Boot 3.3.x, Maven) used consistently.
- **Type consistency:** `Book`, `BookDTO`, `Member`, `MemberDTO`, `Loan`, `BookRepository`, `BookService`, `BookMapper`, `BookNotFoundException`, `GlobalExceptionHandler`, `ErrorResponse`, `BorrowNotAllowedException`, `LoanNotFoundException`, `MemberNotFoundException`, `SecurityConfig` are defined once and referenced by their exact names everywhere they appear.
- **Placeholder check:** No "TBD", "TODO", or "implement later" placeholders. Lab handouts in Tasks 16–25 are intentionally lighter on copy-paste content (only Labs 1 and 2 are written verbatim) — those tasks describe what the handout must cover with enough specificity to write it, and the source-of-truth code for every step exists in the corresponding code task. If executed strictly, the executor will be writing student-facing prose from the spec — acceptable for content creation but worth flagging to the user.
