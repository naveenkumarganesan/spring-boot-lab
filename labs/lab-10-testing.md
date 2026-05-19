# Lab 10 — Testing

**Duration:** ~90 minutes
**Day:** 4, Slot 2

## Objective

Write two **slice tests** for the Library API:

- One `@DataJpaTest` for the repository layer.
- One `@WebMvcTest` for the controller layer, with ≥4 test cases including a 404 and a validation 400.

You'll also run them with Maven and confirm they pass.

## Background

Spring offers three layers of test:

| Test annotation | What it loads | Speed | When to use |
|------------------|---------------|-------|-------------|
| `@SpringBootTest` | The whole app | Slow | End-to-end / integration |
| `@WebMvcTest` | Just MVC + your controller | Fast | Controller-level checks |
| `@DataJpaTest` | Just JPA + repositories + in-memory DB | Fast | Repository-level checks |

Slice tests boot only what they need, so they're fast enough to run on every save.

## Prerequisites

Lab 9 complete.

## Starter state

`git checkout lab-09-end`.

## Steps

### Part A — `BookRepositoryTest` (`@DataJpaTest`)

1. Create `src/test/java/com/example/library/book/BookRepositoryTest.java`:

   ```java
   @DataJpaTest
   class BookRepositoryTest {

       @Autowired
       private BookRepository repository;

       @Test
       void findByTitleAndAuthor_returnsMatches() {
           repository.deleteAll();   // clear seed data from data.sql
           repository.save(new Book("Clean Code", "Robert C. Martin", "isbn-1", 1));
           repository.save(new Book("Clean Architecture", "Robert C. Martin", "isbn-2", 1));
           repository.save(new Book("Effective Java", "Joshua Bloch", "isbn-3", 1));

           List<Book> result = repository
                   .findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("clean", "martin");

           assertThat(result).hasSize(2);
           assertThat(result).extracting(Book::getTitle)
                   .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
       }
   }
   ```

### Part B — `BookControllerTest` (`@WebMvcTest`)

2. Create `src/test/java/com/example/library/book/BookControllerTest.java`. Use `@MockitoBean` to inject a mock `BookService` so the test doesn't touch the database or the real service.

   ```java
   @WebMvcTest(BookController.class)
   @AutoConfigureMockMvc(addFilters = false)   // bypass Spring Security in tests
   class BookControllerTest {

       @Autowired private MockMvc mvc;
       @Autowired private ObjectMapper json;
       @MockitoBean private BookService service;

       @Test
       void list_returnsAllBooks() throws Exception { ... }

       @Test
       void getById_returns200WhenFound() throws Exception { ... }

       @Test
       void getById_returns404WhenMissing() throws Exception {
           when(service.get(999L)).thenThrow(new BookNotFoundException(999L));
           mvc.perform(get("/books/999"))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.status").value(404));
       }

       @Test
       void create_returns400WhenValidationFails() throws Exception {
           BookDTO invalid = new BookDTO();
           invalid.setTitle("");          // violates @NotBlank
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

   Full code in `code/unit2-library-jpa/src/test/java/com/example/library/book/BookControllerTest.java`.

### Part C — Run the tests

```bash
./mvnw test
```

You should see five tests run (the one `@DataJpaTest` + four `@WebMvcTest` cases) plus the default smoke test from the generated project. **All must pass.**

## Verification

```
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Stretch task

Add an **integration test** with Testcontainers:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <scope>test</scope>
</dependency>
```

Use `@Testcontainers` and a `@Container MySQLContainer<?>` to run the whole app against a real MySQL spun up in Docker.

## Common pitfalls

- **`@MockBean` is deprecated** in Spring Boot 3.4+ in favor of `@MockitoBean` (from `org.springframework.test.context.bean.override.mockito`). This lab uses `@MockitoBean` — `@MockBean` still works but throws a deprecation warning.
- **Spring Security blocks the `@WebMvcTest`** — adding security in Lab 11 will cause `BookControllerTest` to fail with 401s. The `@AutoConfigureMockMvc(addFilters = false)` annotation disables the filter chain in tests.
- **`@DataJpaTest` keeps the seeded data from `data.sql`** — that's why we `deleteAll()` at the start. Without it, the assertion `hasSize(2)` fails because `data.sql` also adds "Clean Code" by "Robert C. Martin".

## Checkpoint

Reference solution: `git checkout lab-10-end`.
