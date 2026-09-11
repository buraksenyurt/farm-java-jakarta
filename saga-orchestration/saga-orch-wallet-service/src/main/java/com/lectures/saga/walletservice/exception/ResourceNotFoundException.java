package com.lectures.saga.walletservice.exception;

public abstract class ResourceNotFoundException extends WalletException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
}
