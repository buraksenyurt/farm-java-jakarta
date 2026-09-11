package com.lectures.archguard.domain;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Isbn isbn) {
        super("The book not found " + isbn.value());
    }
}
