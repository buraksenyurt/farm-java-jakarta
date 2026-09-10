package com.lectures.saga.eventservice.service;

// Business Rule Exception sınıfımız
public abstract class BookingException extends RuntimeException {

    protected BookingException(String message) {
        super(message);
    }
}
