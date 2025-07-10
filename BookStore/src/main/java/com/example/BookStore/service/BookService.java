package com.example.BookStore.service;

import com.example.BookStore.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> findAll();
    List<Book> findAllActive();
    Optional<Book> findById(Long id);
    Optional<Book> findByIdAndActive(Long id);
    Book save(Book book);
    void deleteById(Long id);
}
