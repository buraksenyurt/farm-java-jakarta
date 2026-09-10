package com.lectures.gamecatalog.exception;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(Long id) {
        super("ID değeri " + id + " olan oyunu bulamadım :(");
    }
}
