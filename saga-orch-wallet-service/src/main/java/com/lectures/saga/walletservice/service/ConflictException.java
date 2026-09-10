package com.lectures.saga.walletservice.service;

public abstract class ConflictException extends WalletException {

    public ConflictException(String message) {
        super(message);
    }

}
