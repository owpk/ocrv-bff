package com.ocrf.bff.exceptions;

public class BffNoAuthException extends RuntimeException {

    public BffNoAuthException(String message) {
        super(message);
    }
}
