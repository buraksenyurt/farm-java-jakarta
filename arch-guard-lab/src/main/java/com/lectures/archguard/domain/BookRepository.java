package com.lectures.archguard.domain;

import java.util.List;
import java.util.Optional;

/**
 * Port class. Real implementation is on the persistence layer
 */
public interface BookRepository {

    Optional<Book> findByIsbn(Isbn isbn);

    List<Book> findAll();

    void save(Book book);
}
