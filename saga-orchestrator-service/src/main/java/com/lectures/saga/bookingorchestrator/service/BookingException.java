package com.lectures.saga.bookingorchestrator.service;

public abstract class BookingException extends RuntimeException {

    protected BookingException(String message) {
        super(message);
    }
}
