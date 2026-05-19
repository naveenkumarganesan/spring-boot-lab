package com.example.library.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Test
    void findByTitleAndAuthor_returnsMatches() {
        repository.deleteAll();
        repository.save(new Book("Clean Code", "Robert C. Martin", "isbn-1", 1));
        repository.save(new Book("Clean Architecture", "Robert C. Martin", "isbn-2", 1));
        repository.save(new Book("Effective Java", "Joshua Bloch", "isbn-3", 1));

        List<Book> result = repository
                .findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase("clean", "martin");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
    }
}
