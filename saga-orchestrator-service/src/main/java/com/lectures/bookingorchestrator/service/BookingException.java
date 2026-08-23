package com.lectures.bookingorchestrator.service;

public abstract class BookingException extends RuntimeException {

    protected BookingException(String message) {
        super(message);
    }
}
