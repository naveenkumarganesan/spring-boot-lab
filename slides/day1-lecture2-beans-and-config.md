---
title: Day 1 · Lecture 2 — Beans and configuration
duration: 60 min
---

# Day 1 · Lecture 2
## Beans, injection, and configuration

---

## What is a bean?

> A Java object whose lifecycle is managed by the Spring container.

- Created **by Spring**, not by your code.
- Lives in the **application context** (a singleton registry).
- Destroyed when the app shuts down.
- Other components ask the container for it instead of creating it.

---

## The four "stereotype" annotations

All of these create a bean. The names just clarify intent.

| Annotation | Conventional meaning |
|-------------|-----------------------|
| `@Component` | Any Spring-managed bean |
| `@Service` | A class containing business logic |
| `@Repository` | A class talking to the database |
| `@Controller` / `@RestController` | A class handling HTTP requests |

Spring treats them identically. Use the one that documents intent best.

---

## Constructor injection (preferred)

```java
@RestController
public class HelloController {
    private final GreetingService greetingService;

    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }
}
```

- The field is `final` — immutable after construction.
- Spring sees the constructor parameter, looks up a `GreetingService` bean, passes it in.
- If no `GreetingService` bean exists, the app **fails fast at startup**, not later when you'd hit a NullPointerException.

---

## Field injection (avoid)

```java
@RestController
public class HelloController {
    @Autowired
    private GreetingService greetingService;   // bad
}
```

Problems:

- Field isn't `final`.
- Hard to unit-test (you can't pass mocks via constructor).
- Hides dependencies — they don't appear in the public API.

Use constructor injection. The Spring docs themselves recommend it.

---

## `application.properties`

The default config file at `src/main/resources/application.properties`. Key-value pairs:

```properties
server.port=8081
spring.application.name=library
greeting.prefix=Hello
```

YAML alternative (`application.yml`) is also supported. Same keys, different syntax.

---

## Reading a property: `@Value`

```java
public class EnglishGreetingService implements GreetingService {
    private final String prefix;

    public EnglishGreetingService(@Value("${greeting.prefix:Hello}") String prefix) {
        this.prefix = prefix;
    }
}
```

- `${greeting.prefix}` looks up the property.
- `:Hello` is a default — used when the property is missing.

---

## `@ConfigurationProperties` (for grouped config)

When you have many related properties, bind them to a class:

```java
@ConfigurationProperties("greeting")
public class GreetingProperties {
    private String prefix;
    private String suffix;
    // getters and setters
}
```

```properties
greeting.prefix=Hello
greeting.suffix=!
```

Better than 10 separate `@Value` annotations.

---

## Profiles

You often need different config per environment:

- **dev** — local H2 database, verbose logging.
- **prod** — MySQL, INFO logs only.
- **test** — in-memory everything.

Profile files override the default:

- `application.properties` — always loaded.
- `application-dev.properties` — loaded only if profile `dev` is active.
- `application-prod.properties` — loaded only if profile `prod` is active.

Activate with `--spring.profiles.active=prod` on the command line.

---

## `@Profile` on a bean

```java
@Service
@Profile("hi")
public class HindiGreetingService implements GreetingService { ... }

@Service
@Profile({"default", "en"})
public class EnglishGreetingService implements GreetingService { ... }
```

Only the bean matching the active profile is registered. So when you inject `GreetingService`, you get the right one for the current environment.

---

## Bean lifecycle (the short version)

1. Spring starts.
2. Component scan finds your `@Service`/`@Component`/`@Controller` classes.
3. For each, Spring resolves constructor parameters (more beans) and instantiates the bean.
4. Beans live in the application context for the life of the app.
5. On shutdown, Spring calls any `@PreDestroy` methods, then exits.

---

## Lab 2 preview

Define a `GreetingService` interface, two implementations (English / Hindi), wire it up, switch them with `@Profile`.

```bash
$ curl localhost:8080/hello/Asha
Hello, Asha!

$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=hi
$ curl localhost:8080/hello/Asha
Namaste, Asha!
```

---

## Questions?
