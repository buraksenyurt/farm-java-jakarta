package com.lectures.archguard.domain;

import java.time.LocalDate;
import java.util.Objects;

// Domain Entity
public class Book {

    private final Isbn isbn;
    private final String title;
    private final String author;
    private boolean borrowed;
    private LocalDate borrowedAt;

    public Book(Isbn isbn, String title, String author) {
        this.isbn = Objects.requireNonNull(isbn, "isbn");
        this.title = Objects.requireNonNull(title, "title");
        this.author = Objects.requireNonNull(author, "author");
    }

    public void borrow(LocalDate today) {
        if (borrowed) {
            throw new BookAlreadyBorrowedException(isbn);
        }
        this.borrowed = true;
        this.borrowedAt = today;
    }

    public void giveBack() {
        this.borrowed = false;
        this.borrowedAt = null;
    }

    public Isbn isbn() {
        return isbn;
    }

    public String title() {
        return title;
    }

    public String author() {
        return author;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public LocalDate borrowedAt() {
        return borrowedAt;
    }
}
