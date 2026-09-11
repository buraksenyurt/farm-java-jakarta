package com.lectures.archguard.domain;

// A simple Value Object
public record Isbn(String value) {

    public Isbn {
        if (value == null || value.isBlank()) {
            throw new InvalidIsbnException("Empty ISBN value violation");
        }
        String normalized = value.replace("-", "");
        if (normalized.length() != 13) {
            throw new InvalidIsbnException("It must be 13 length: " + value);
        }
    }
}
