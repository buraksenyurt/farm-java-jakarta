package com.lectures.eventticketing.exception;

// Business Rule Exception sınıfımız
public abstract class BookingException extends RuntimeException {

    protected BookingException(String message) {
        super(message);
    }
}
