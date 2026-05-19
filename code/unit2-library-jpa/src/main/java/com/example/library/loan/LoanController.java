package com.example.library.loan;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

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
