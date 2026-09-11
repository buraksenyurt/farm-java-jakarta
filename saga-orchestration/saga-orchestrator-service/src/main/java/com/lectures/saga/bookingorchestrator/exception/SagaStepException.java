package com.lectures.saga.bookingorchestrator.exception;

public class SagaStepException extends BusinessConflictException {

    public SagaStepException(String message) {
        super(message);
    }
}
