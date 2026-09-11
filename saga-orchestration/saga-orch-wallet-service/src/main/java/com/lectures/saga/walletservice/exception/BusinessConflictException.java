package com.lectures.saga.walletservice.exception;

public abstract class BusinessConflictException extends WalletException {

    public BusinessConflictException(String message) {
        super(message);
    }

}
