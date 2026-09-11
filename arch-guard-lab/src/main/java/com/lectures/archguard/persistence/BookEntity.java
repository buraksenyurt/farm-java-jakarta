package com.lectures.archguard.persistence;

import com.lectures.archguard.domain.Book;
import com.lectures.archguard.domain.Isbn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @Column(name = "isbn", length = 13)
    private String isbn;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "borrowed")
    private boolean borrowed;

    public Book toDomain() {
        Book book = new Book(new Isbn(isbn), title, author);
        if (borrowed) {
            book.borrow(java.time.LocalDate.now());
        }
        return book;
    }

    public static BookEntity fromDomain(Book book) {
        BookEntity entity = new BookEntity();
        entity.isbn = book.isbn().value();
        entity.title = book.title();
        entity.author = book.author();
        entity.borrowed = book.isBorrowed();
        return entity;
    }
}