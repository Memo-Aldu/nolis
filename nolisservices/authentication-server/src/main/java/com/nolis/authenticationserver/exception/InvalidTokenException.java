package com.nolis.authenticationserver.exception;

import java.io.Serial;

public class InvalidTokenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String message, Exception e) {
        super(message, e);
    }
    public InvalidTokenException(String message) {
        super(message);
    }
}
