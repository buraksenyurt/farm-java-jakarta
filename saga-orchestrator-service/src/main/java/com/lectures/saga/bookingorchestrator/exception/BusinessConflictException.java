package com.lectures.saga.bookingorchestrator.exception;

public abstract class BusinessConflictException extends BookingException {

    public BusinessConflictException(String message) {
        super(message);
    }

}
