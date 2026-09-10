package com.lectures.eventticketing.exception;

public abstract class ResourceNotFoundException extends BookingException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
}
