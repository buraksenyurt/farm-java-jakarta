package com.lectures.bookingorchestrator.service;

public class SagaStepException extends ConflictException {

    public SagaStepException(String message) {
        super(message);
    }
}
