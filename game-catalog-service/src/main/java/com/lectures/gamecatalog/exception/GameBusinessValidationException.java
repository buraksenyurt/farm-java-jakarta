package com.lectures.gamecatalog.exception;


public class GameBusinessValidationException extends RuntimeException {
    public GameBusinessValidationException(String message){
        super(message);
    }
}
