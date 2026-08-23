package com.lectures.walletservice.service;

public abstract class ConflictException extends BookingException {

    public ConflictException(String message) {
        super(message);
    }

}
