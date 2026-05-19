---
title: Day 4 · Lecture 7 — Testing
duration: 90 min
---

# Day 4 · Lecture 7
## Testing Spring Boot apps

---

## The testing pyramid

```
                 /\
                /  \    Few    →  full-app integration tests
               /    \            (slow, brittle, high confidence)
              /------\
             /        \  Some   →  slice tests (controller / repository)
            /          \         (fast, focused)
           /------------\
          /              \ Many →  pure unit tests (services, mappers, helpers)
         /________________\        (very fast, narrow)
```

Most of your tests should be at the bottom. A few slice tests give you HTTP and JPA confidence. The full-app tests are a smaller set that prove everything composes.

---

## Spring's test annotations

| Annotation | What it loads | Speed | Used for |
|-------------|---------------|-------|----------|
| `@SpringBootTest` | The entire app | Slow | End-to-end / integration |
| `@WebMvcTest(C.class)` | MVC layer + controller `C` | Fast | Controller tests |
| `@DataJpaTest` | JPA + repositories + in-memory DB | Fast | Repository tests |
| `@JsonTest` | Jackson only | Very fast | JSON marshalling tests |

The "slice" tests boot only what they need. That's why they're fast.

---

## A `@DataJpaTest`

```java
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Test
    void findByTitleAndAuthor_returnsMatches() {
        repository.save(new Book("Clean Code", "Robert C. Martin", "isbn-1", 1));
        ...
        List<Book> result = repository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("clean", "martin");
        assertThat(result).hasSize(2);
    }
}
```

- Spring boots only JPA, no controllers, no security.
- Uses an in-memory H2 by default — your real database config is ignored.
- Each test runs in a transaction, rolled back at the end. Tests don't leak data into each other.

---

## A `@WebMvcTest`

```java
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired private MockMvc mvc;
    @MockitoBean private BookService service;   // mock — real one not loaded

    @Test
    void getById_returns200WhenFound() throws Exception {
        when(service.get(1L)).thenReturn(sample(1L, "Clean Code"));

        mvc.perform(get("/books/1"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.title").value("Clean Code"));
    }
}
```

- Only the controller and Spring MVC are loaded.
- The service is **mocked** with Mockito — no DB, no real logic.
- `MockMvc` simulates HTTP requests without starting a server.

---

## `@MockitoBean` (Spring Boot 3.4+)

Replaces `@MockBean` (deprecated in 3.4):

```java
@MockitoBean
private BookService service;
```

Behind the scenes, Mockito creates a mock instance and Spring registers it in the application context so the controller picks it up. You then program the mock with `when(...).thenReturn(...)`.

---

## MockMvc cheat sheet

```java
// GET, expect 200
mvc.perform(get("/books"))
   .andExpect(status().isOk())
   .andExpect(jsonPath("$[0].title").value("Clean Code"));

// POST JSON, expect 400
mvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidDto)))
   .andExpect(status().isBadRequest())
   .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));

// Header check
mvc.perform(post("/books").content(...))
   .andExpect(header().exists("Location"));
```

`jsonPath` uses JsonPath syntax: `$` is the root, `$.field`, `$[0]`, `$.list[*].name`, etc.

---

## AssertJ — fluent assertions

```java
assertThat(result)
    .hasSize(2)
    .extracting(Book::getTitle)
    .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");

assertThat(book.getCopies()).isEqualTo(3);
assertThat(loan.getReturnedOn()).isNull();
assertThat(books).allMatch(b -> b.getCopies() >= 0);
```

Better error messages than `Assertions.assertEquals(...)`, more readable chains.

---

## Test naming conventions

Pick one and stick with it:

```
methodName_condition_expectedResult
get_validId_returnsBook
get_unknownId_throwsNotFound
borrow_outOfCopies_throwsConflict
```

The test name should explain what the test does without reading the body.

---

## TDD in passing

If you're truly disciplined:

1. Write the failing test (red).
2. Write minimum code to pass (green).
3. Refactor (still green).
4. Commit.

You don't have to be that strict, but the discipline of writing the test first is genuinely useful when you're unsure what the API should look like. The test forces you to decide.

---

## What to test (and what not to)

**Test:**
- Business rules in the service ("can't borrow if copies <= 0").
- Repository custom finders ("does my query actually return the right rows?").
- Controller validation ("does an empty title return 400 with a field error?").
- The happy path end-to-end (one `@SpringBootTest`).

**Don't bother testing:**
- Getters and setters.
- Generated code.
- Spring framework behavior (e.g., that `@Autowired` injects — that's Spring's job, not yours).

---

## Lab 10 preview

Write:
- 1 `@DataJpaTest` for the custom finder on `BookRepository`.
- 1 `@WebMvcTest` for `BookController` with **at least 4 cases**: list 200, get-by-id 200, get-by-id 404, POST invalid → 400.

Run with `./mvnw test`. All must pass.

---

## Questions?
