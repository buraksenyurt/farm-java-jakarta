package com.lectures.saga.walletservice.exception;

// Business Rule Exception sınıfımız
public abstract class WalletException extends RuntimeException {

    protected WalletException(String message) {
        super(message);
    }
}
