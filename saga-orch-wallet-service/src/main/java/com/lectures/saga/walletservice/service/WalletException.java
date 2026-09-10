package com.lectures.saga.walletservice.service;

// Business Rule Exception sınıfımız
public abstract class WalletException extends RuntimeException {

    protected WalletException(String message) {
        super(message);
    }
}
