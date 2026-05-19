package com.example.library.book;

import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDTO toDto(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setCopies(book.getCopies());
        return dto;
    }

    public Book toEntity(BookDTO dto) {
        Book b = new Book(dto.getTitle(), dto.getAuthor(), dto.getIsbn(), dto.getCopies());
        b.setId(dto.getId());
        return b;
    }
}
