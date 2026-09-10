package com.lectures.eventticketing.exception;

public abstract class BusinessConflictException extends BookingException {

    public BusinessConflictException(String message) {
        super(message);
    }

}
