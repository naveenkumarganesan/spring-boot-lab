# Lab 2 — Beans and Configuration

**Duration:** ~75 minutes
**Day:** 1, Slot 4

## Objective

Inject a service into a controller using constructor injection, read configuration from `application.properties`, and switch between two implementations using `@Profile`.

## Prerequisites

Lab 1 complete.

## Starter state

`git checkout lab-01-end` (or your own Lab 1 result).

## Steps

1. Create a new package `com.example.library.greeting`.
2. Inside it, create an interface `GreetingService`:

   ```java
   package com.example.library.greeting;

   public interface GreetingService {
       String greet(String name);
   }
   ```

3. Create `EnglishGreetingService` in the same package. Annotate with `@Service` and `@Profile({"default", "en"})`. Inject a `@Value("${greeting.prefix:Hello}") String prefix` via the constructor. `greet(name)` should return `prefix + ", " + name + "!"`.

   ```java
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

4. Create `HindiGreetingService` annotated with `@Service` and `@Profile("hi")`. `greet(name)` returns `"Namaste, " + name + "!"`.

5. Modify `HelloController` — take a `GreetingService` via constructor injection. Replace the `/hello` endpoint with `GET /hello/{name}` calling `greetingService.greet(name)`.

   ```java
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

6. Add to `src/main/resources/application.properties`:

   ```properties
   greeting.prefix=Hello
   ```

7. Run the app (default profile). Test:

   ```bash
   curl http://localhost:8080/hello/Asha
   # → Hello, Asha!
   ```

8. Stop the app. Re-run with the `hi` profile active. In IntelliJ: **Edit Configurations** → **Modify options → Active profiles** = `hi`. Or from the command line:

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=hi
   curl http://localhost:8080/hello/Asha
   # → Namaste, Asha!
   ```

## Expected output

- Default profile: `Hello, Asha!`
- `hi` profile: `Namaste, Asha!`

## Stretch task

- Add a `@ConfigurationProperties("greeting")` class to bind `greeting.*` properties (instead of `@Value`).
- Add a `TamilGreetingService` under `@Profile("ta")` returning "Vanakkam, ...!".

## Common pitfalls

- **`NoUniqueBeanDefinitionException`** at startup — only one `GreetingService` should match the active profile. Check the `@Profile` values on both implementations.
- **Property not resolved** — `@Value("${greeting.prefix}")` without a default crashes if the property is missing. The `${greeting.prefix:Hello}` syntax provides a fallback.

## Checkpoint

Reference solution: `git checkout lab-02-end` inside `code/unit1-library-inmem/`.
