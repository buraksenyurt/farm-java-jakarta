package com.lectures.saga.bookingorchestrator.exception;

public abstract class BookingException extends RuntimeException {

    protected BookingException(String message) {
        super(message);
    }
}
