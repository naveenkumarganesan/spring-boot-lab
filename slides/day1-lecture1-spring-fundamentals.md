---
title: Day 1 · Lecture 1 — Spring fundamentals
duration: 90 min
---

# Day 1 · Lecture 1
## Spring Boot — what and why

---

## What we're doing this week

A 4-day hands-on Spring Boot seminar.

- 12 labs across 4 days
- Two units: in-memory REST API, then production-shape with DB, layers, validation, tests, security, Docker
- We're building a **Library Management System** twice — once thinly, then properly

By Day 4 evening you'll have a Docker container you can show off.

---

## The problem Spring solves

Java enterprise apps before Spring:

- XML everywhere
- Lots of "glue" code (`new` this, `new` that)
- Tightly coupled — testing was painful
- Wiring a service that needed a DB, logger, and config took 50 lines of plumbing

---

## Inversion of Control (IoC)

> "Don't call us, we'll call you."

Instead of:

```java
Service s = new Service(new Repo(new Db("url")));
```

You declare what you **need**:

```java
class HelloController {
    HelloController(GreetingService greetingService) { ... }
}
```

The Spring container builds the dependency graph for you. **One instance**, shared across the app.

---

## Dependency Injection — by hand

```java
class BookController {
    private final BookService service;

    BookController(BookService service) {   // constructor injection
        this.service = service;
    }
}
```

You never write `new BookService()`. Spring does — once — and hands you the same instance every time.

---

## What "Spring" actually is

Three layers:

| Layer | What it provides |
|-------|-------------------|
| **Spring Core** | IoC container, dependency injection |
| **Spring Framework** | MVC, Data, Security, etc. — modules built on Core |
| **Spring Boot** | Sensible defaults + starters + embedded server, so you can `java -jar` your app |

---

## What Spring Boot adds on top

- **Auto-configuration** — "If `spring-boot-starter-web` is on the classpath, you probably want an embedded Tomcat. Done."
- **Starters** — one Maven dependency pulls in 12 related libraries with matching versions.
- **Embedded server** — no WAR file, no Tomcat install. The server is bundled.
- **Convention over configuration** — defaults that work, override only what's different.

---

## `@SpringBootApplication` decoded

It's three annotations rolled into one:

```java
@SpringBootApplication
public class LibraryApplication { ... }
```

- `@Configuration` — this class can define beans.
- `@EnableAutoConfiguration` — turn on the auto-config magic.
- `@ComponentScan` — scan this package and subpackages for `@Component`, `@Service`, `@Controller`.

---

## Maven in 90 seconds

- `pom.xml` is your project's recipe.
- `<parent>spring-boot-starter-parent</parent>` inherits Spring Boot's version management — you don't pick versions for every dependency.
- `<dependencies>` declares what you need.
- `mvn package` compiles, tests, and produces a jar.
- `mvnw` (Maven wrapper) ships with the project — students don't need Maven installed.

---

## What `start.spring.io` generates

```
library/
├── pom.xml                  Maven build file
├── mvnw, mvnw.cmd           Maven wrapper scripts
├── .mvn/                    Wrapper internals
└── src/
    ├── main/
    │   ├── java/com/example/library/
    │   │   └── LibraryApplication.java   ← entry point
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/example/library/
            └── LibraryApplicationTests.java
```

That's it. **One Java file** is all you have to make Spring Boot run.

---

## Lab 1 preview

Build the project. Add a `@RestController`. Hit it with curl.

```bash
$ curl http://localhost:8080/hello
Hello, Spring Boot!
```

If you get that response, you're done. 75 minutes.

---

## Questions?
