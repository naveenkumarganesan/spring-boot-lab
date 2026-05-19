# Lab 11 — Profiles, Security, and Ops

**Duration:** ~75 minutes
**Day:** 4, Slot 4

## Objective

Three small but real additions:

1. **A `prod` profile** that switches H2 to MySQL via a separate properties file.
2. **Actuator endpoints** so operators can check the app is healthy.
3. **HTTP Basic authentication** protecting `/loans/**` (anyone can read books and members, but borrowing requires auth).

## Prerequisites

Lab 10 complete.

## Starter state

`git checkout lab-10-end`.

## Steps

### Part A — Maven dependencies

Add to `pom.xml`:

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

Refresh Maven (in IntelliJ: the Maven sidebar, the circular-refresh icon).

### Part B — Configure Actuator + default user in `application.properties`

Append:

```properties
management.endpoints.web.exposure.include=health,info
management.info.env.enabled=true
info.app.name=Library Management
info.app.version=1.0.0

spring.security.user.name=admin
spring.security.user.password=admin
```

### Part C — Create `application-prod.properties` (MySQL profile)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/librarydb
spring.datasource.username=library_user
spring.datasource.password=library_pass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

spring.sql.init.mode=never
spring.h2.console.enabled=false
```

This file only applies when the `prod` profile is active. The defaults (H2) keep working otherwise.

### Part D — `SecurityConfig`

Create `com.example.library.security.SecurityConfig`:

```java
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
            .headers(h -> h.frameOptions(f -> f.sameOrigin()));   // H2 console needs frames
        return http.build();
    }
}
```

### Part E — Update the Lab 10 controller test

Spring Security now applies its filter chain to every request — including in `@WebMvcTest`. Without intervention, the `BookControllerTest` GETs will start returning 401.

Add this annotation to `BookControllerTest`:

```java
@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)   // <-- new
class BookControllerTest { ... }
```

This disables the security filter chain in the test only. Re-run tests:

```bash
./mvnw test
```

All previous tests must still pass.

## Verification

### Dev profile (default)

```bash
./mvnw spring-boot:run

# Public endpoints — still work
curl http://localhost:8080/books

# /loans is now protected
curl -i -X POST 'http://localhost:8080/loans?bookId=1&memberId=1'
# HTTP/1.1 401 Unauthorized

curl -u admin:admin -i -X POST 'http://localhost:8080/loans?bookId=1&memberId=1'
# HTTP/1.1 201 Created   (you'll need to POST a member first if you haven't)

# Actuator endpoints
curl http://localhost:8080/actuator/health
# {"status":"UP"}
curl http://localhost:8080/actuator/info
# {"app":{"name":"Library Management","version":"1.0.0"}}
```

### Prod profile

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

You don't need a running MySQL to see the profile is loaded — look for this log line:

```
The following 1 profile is active: "prod"
```

A connection failure on startup is expected if MySQL isn't running locally. (If you do want to try it: `brew install mysql`, start it, create the `librarydb` schema and the `library_user`.)

## Stretch task

Replace HTTP Basic with a JWT-based scheme: add an `/auth/login` endpoint that accepts username/password and returns a JWT, and a filter that validates the token on every request. Use `io.jsonwebtoken:jjwt-api` for the JWT library.

## Common pitfalls

- **`@WebMvcTest` returns 401 after this lab** — Spring Security applies its filter chain even in slice tests. Use `@AutoConfigureMockMvc(addFilters = false)` (or `.with(user("admin"))` per request).
- **H2 console returns a blank page** — Spring Security's default `frameOptions=DENY` blocks the iframe-based console. The config above explicitly sets `sameOrigin`.
- **`/actuator/env` returns 404** — by default, only `health` and `info` are exposed. Add `env` to `management.endpoints.web.exposure.include` if you want it (be careful — `env` can leak secrets).
- **CSRF errors on POST** — for a non-browser API, you typically disable CSRF (`csrf(c -> c.disable())`). If this were a browser-facing app, you'd keep it on and include a CSRF token.

## Checkpoint

Reference solution: `git checkout lab-11-end`.
