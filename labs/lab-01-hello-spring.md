# Lab 1 — Hello, Spring Boot

**Duration:** ~75 minutes
**Day:** 1, Slot 2

## Objective

Generate a Spring Boot project from scratch, run it, and expose a single `GET /hello` endpoint.

## Prerequisites

JDK 21, IntelliJ Community, Git installed (see [`../README.md`](../README.md)).

## Starter state

None — you create the project from scratch.

## Steps

1. Open https://start.spring.io in a browser.
2. Configure: Project = Maven, Language = Java, Spring Boot = 3.5.x (the default), Group = `com.example`, Artifact = `library`, Name = `library`, Package = `com.example.library`, Java = 21.
3. Add dependency: **Spring Web**.
4. Click **Generate**, download the zip, unzip into your workspace.
5. Open the project in IntelliJ. Wait for Maven to import (first time may take a few minutes — it's downloading the Spring Boot dependencies).
6. Find `LibraryApplication.java`. Right-click → **Run 'LibraryApplication.main()'**.
7. Watch the console — wait for "Started LibraryApplication in N seconds".
8. Open `http://localhost:8080` in a browser. You should see a Whitelabel error page — that's expected; we haven't defined the root endpoint.
9. Create a new class `HelloController` in the same package as `LibraryApplication`:

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

10. Stop the app (the red square button in IntelliJ). Re-run it so the new class is picked up.
11. Open a terminal and run:

    ```bash
    curl http://localhost:8080/hello
    ```

## Expected output

```
$ curl http://localhost:8080/hello
Hello, Spring Boot!
```

## Stretch task

Add a second endpoint `GET /hello/{name}` that returns `"Hello, <name>!"`. Use `@PathVariable String name`.

## Common pitfalls

- **Port 8080 already in use** — set `server.port=8081` in `src/main/resources/application.properties` and try again.
- **IntelliJ doesn't auto-import `@RestController` and `@GetMapping`** — press Alt+Enter on the red squiggly text to import.
- **Wrong Java version** — File → Project Structure → Project SDK must be set to 21.

## Checkpoint

Reference solution: `git checkout lab-01-end` inside `code/unit1-library-inmem/` (relative to repo root).
