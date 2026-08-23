package com.lectures.eventservice.service;

public abstract class ConflictException extends BookingException {

    public ConflictException(String message) {
        super(message);
    }

}
