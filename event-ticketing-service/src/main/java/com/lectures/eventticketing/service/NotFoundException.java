package com.lectures.eventticketing.service;

public abstract class NotFoundException extends BookingException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
}
