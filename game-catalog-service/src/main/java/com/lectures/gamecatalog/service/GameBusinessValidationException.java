package com.lectures.gamecatalog.service;


public class GameBusinessValidationException extends RuntimeException {
    public GameBusinessValidationException(String message){
        super(message);
    }
}
