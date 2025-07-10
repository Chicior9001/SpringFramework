package com.example.BookStore.service.impl;

import com.example.BookStore.model.Book;
import com.example.BookStore.repository.BookRepository;
import com.example.BookStore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> findAllActive() {
        return bookRepository.findAll().stream().filter(Book::isActive).toList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Optional<Book> findByIdAndActive(Long id) {
        return bookRepository.findByIdAndIsActiveTrue(id);
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteById(Long id) {
        if(bookRepository.existsById(id)) {
            Optional<Book> book = bookRepository.findByIdAndIsActiveTrue(id);
            if(book.isEmpty()) {
                throw new IllegalStateException("Book " + id + " not found.");
            } else {
                book.get().setActive(false);
                bookRepository.save(book.get());
            }
        }
    }
}
