package com.example.library.loan;

import com.example.library.book.Book;
import com.example.library.member.Member;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate borrowedOn;

    private LocalDate returnedOn;

    public Loan() {}

    public Loan(Book book, Member member, LocalDate borrowedOn) {
        this.book = book;
        this.member = member;
        this.borrowedOn = borrowedOn;
    }

    public Long getId() { return id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public LocalDate getBorrowedOn() { return borrowedOn; }
    public void setBorrowedOn(LocalDate borrowedOn) { this.borrowedOn = borrowedOn; }
    public LocalDate getReturnedOn() { return returnedOn; }
    public void setReturnedOn(LocalDate returnedOn) { this.returnedOn = returnedOn; }
}
