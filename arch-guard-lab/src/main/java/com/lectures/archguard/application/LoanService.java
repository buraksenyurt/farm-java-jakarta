package com.lectures.archguard.application;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lectures.archguard.domain.Book;
import com.lectures.archguard.domain.BookNotFoundException;
import com.lectures.archguard.domain.BookRepository;
import com.lectures.archguard.domain.Isbn;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LoanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanService.class);

    private final BookRepository bookRepository;

    protected LoanService() {
        this.bookRepository = null;
    }

    @Inject
    public LoanService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> listAll() {
        return bookRepository.findAll();
    }

    public Book borrow(Isbn isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        book.borrow(LocalDate.now());
        bookRepository.save(book);
        LOGGER.info("Book borrowed {}", isbn.value());
        return book;
    }
}
