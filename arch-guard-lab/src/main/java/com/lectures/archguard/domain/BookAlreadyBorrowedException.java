package com.lectures.archguard.domain;

public class BookAlreadyBorrowedException extends RuntimeException {

    public BookAlreadyBorrowedException(Isbn isbn) {
        super("The book has been already borrowed " + isbn.value());
    }
}