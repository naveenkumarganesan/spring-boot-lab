package com.example.library.book;

import com.example.library.common.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;
    private final BookMapper mapper;

    public BookService(BookRepository repository, BookMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<BookDTO> listAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public BookDTO get(Long id) {
        return repository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public BookDTO create(BookDTO dto) {
        Book saved = repository.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    public BookDTO update(Long id, BookDTO dto) {
        Book existing = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        existing.setTitle(dto.getTitle());
        existing.setAuthor(dto.getAuthor());
        existing.setIsbn(dto.getIsbn());
        existing.setCopies(dto.getCopies());
        return mapper.toDto(repository.save(existing));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new BookNotFoundException(id);
        repository.deleteById(id);
    }

    public List<BookDTO> search(String title, String author) {
        return repository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author)
                .stream().map(mapper::toDto).toList();
    }
}
