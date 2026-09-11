package com.lectures.saga.eventservice.exception;

public abstract class ResourceNotFoundException extends BookingException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
}
