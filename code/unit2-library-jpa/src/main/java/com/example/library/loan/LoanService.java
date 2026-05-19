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
