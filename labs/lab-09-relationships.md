# Lab 9 — Relationships (Member ↔ Loan ↔ Book)

**Duration:** ~90 minutes
**Day:** 3, Slot 5

## Objective

Introduce two more entities: `Member` (someone who borrows books) and `Loan` (a record that a member has borrowed a particular book). Use JPA `@ManyToOne` to wire up the relationships, and expose borrow / return endpoints with proper business rules.

## Prerequisites

Lab 8 complete.

## Starter state

`git checkout lab-08-end`.

## Schema

```
Book        Member
  |           |
  | (M:1)     | (M:1)
  v           v
       Loan
   (book_id, member_id, borrowed_on, returned_on)
```

A `Loan` references one `Book` and one `Member`. `returnedOn` is null while the book is checked out; it gets set when the book is returned.

## Steps

1. **Create `member` package and `Member` entity:**

   ```java
   @Entity
   @Table(name = "members")
   public class Member {
       @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       @Column(nullable = false) private String name;
       @Column(unique = true, nullable = false) private String email;
       private LocalDate registeredOn;
       // constructors, getters, setters
   }
   ```

2. **`MemberRepository`** extending `JpaRepository<Member, Long>`.

3. **`MemberDTO`** with `@NotBlank` on `name`, `@NotBlank @Email` on `email`.

4. **`MemberService`** with `listAll`, `get(id)`, `create(dto)`. Throw `MemberNotFoundException` for misses.

5. **`MemberController`** with `GET /members`, `GET /members/{id}`, `POST /members` (validated).

6. **Create `loan` package and `Loan` entity:**

   ```java
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

       @Column(nullable = false) private LocalDate borrowedOn;
       private LocalDate returnedOn;
       // constructors, getters, setters
   }
   ```

7. **`LoanRepository`** with a custom finder:

   ```java
   public interface LoanRepository extends JpaRepository<Loan, Long> {
       List<Loan> findByMemberIdAndReturnedOnIsNull(Long memberId);
   }
   ```

8. **`LoanService`** — this is the interesting part. The borrow operation must be transactional:

   ```java
   @Service
   public class LoanService {
       // inject loans, books, members repos

       @Transactional
       public Loan borrow(Long bookId, Long memberId) {
           Book book = books.findById(bookId).orElseThrow(...);
           Member member = members.findById(memberId).orElseThrow(...);
           if (book.getCopies() <= 0)
               throw new BorrowNotAllowedException("No copies available for book " + bookId);
           book.setCopies(book.getCopies() - 1);
           books.save(book);
           return loans.save(new Loan(book, member, LocalDate.now()));
       }

       @Transactional
       public Loan returnLoan(Long loanId) {
           Loan loan = loans.findById(loanId).orElseThrow(...);
           if (loan.getReturnedOn() != null)
               throw new BorrowNotAllowedException("Loan " + loanId + " already returned");
           loan.setReturnedOn(LocalDate.now());
           Book book = loan.getBook();
           book.setCopies(book.getCopies() + 1);
           books.save(book);
           return loans.save(loan);
       }
   }
   ```

9. Add new exceptions to `common`:
   - `MemberNotFoundException` → 404
   - `LoanNotFoundException` → 404
   - `BorrowNotAllowedException` → 409 Conflict

10. Extend `GlobalExceptionHandler` to handle them.

11. **`LoanController`:**

    ```java
    @PostMapping
    public ResponseEntity<Loan> borrow(@RequestParam Long bookId, @RequestParam Long memberId) {
        Loan loan = service.borrow(bookId, memberId);
        return ResponseEntity.created(URI.create("/loans/" + loan.getId())).body(loan);
    }

    @PutMapping("/{id}/return")
    public Loan returnLoan(@PathVariable Long id) {
        return service.returnLoan(id);
    }
    ```

## Verification

```bash
# Create a member
curl -i -X POST http://localhost:8080/members \
    -H 'Content-Type: application/json' \
    -d '{"name":"Asha","email":"asha@example.com"}'
# HTTP/1.1 201, body contains id

# Borrow book 1 with member 1
curl -i -X POST 'http://localhost:8080/loans?bookId=1&memberId=1'
# HTTP/1.1 201

# Check the book's copies count dropped
curl http://localhost:8080/books/1
# copies decreased by one

# Return the loan
curl -i -X PUT http://localhost:8080/loans/1/return
# HTTP/1.1 200, returnedOn set

# Copies restored
curl http://localhost:8080/books/1
```

## Stretch task

Add a bidirectional relationship: `Member` has a `List<Loan>` field annotated `@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)`. Add `@JsonIgnore` on the inverse side to break Jackson's infinite recursion when serializing.

## Common pitfalls

- **Infinite JSON loop** — adding `Loan.member` and `Member.loans` without `@JsonIgnore` makes Jackson recurse forever. Either keep it unidirectional (as the baseline does) or break the cycle with `@JsonIgnore`.
- **Borrow doesn't decrement copies** — the `LoanService.borrow` method must be `@Transactional` so the book save and the loan save commit together. Without it, the book change may not persist if the loan save fails.
- **`LazyInitializationException` when serializing the loan** — by default, `@ManyToOne` is `EAGER`, so this shouldn't happen. If you change it to `LAZY` for performance, you'll need to handle session boundaries.

## Checkpoint

Reference solution: `git checkout lab-09-end`.
