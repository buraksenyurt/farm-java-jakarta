package com.lectures.archguard.api;

import com.lectures.archguard.domain.Book;

public record BookView(String isbn, String title, String author, boolean borrowed) {

    public static BookView from(Book book) {
        return new BookView(book.isbn().value(), book.title(), book.author(), book.isBorrowed());
    }
}
