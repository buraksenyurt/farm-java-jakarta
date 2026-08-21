package com.lectures.eventticketing.service;

public abstract class ConflictException extends BookingException {

    public ConflictException(String message) {
        super(message);
    }

}
