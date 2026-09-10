package com.lectures.saga.eventservice.exception;

public abstract class BusinessConflictException extends BookingException {

    public BusinessConflictException(String message) {
        super(message);
    }

}
