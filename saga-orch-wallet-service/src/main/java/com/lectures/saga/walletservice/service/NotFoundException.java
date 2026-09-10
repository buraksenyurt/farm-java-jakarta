package com.lectures.saga.walletservice.service;

public abstract class NotFoundException extends WalletException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
}
