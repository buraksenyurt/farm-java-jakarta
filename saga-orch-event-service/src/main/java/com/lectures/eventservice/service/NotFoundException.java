package com.lectures.eventservice.service;

public abstract class NotFoundException extends BookingException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
}
