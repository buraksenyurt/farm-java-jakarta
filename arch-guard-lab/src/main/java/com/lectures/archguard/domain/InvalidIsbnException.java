package com.lectures.archguard.domain;
        
public class InvalidIsbnException extends RuntimeException {

    public InvalidIsbnException(String message) {
        super(message);
    }
}
