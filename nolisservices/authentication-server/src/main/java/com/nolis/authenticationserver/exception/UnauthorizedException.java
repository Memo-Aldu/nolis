package com.nolis.authenticationserver.exception;

import java.io.Serial;

public class UnauthorizedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public UnauthorizedException(String message, Exception cause) {
        super(message, cause);
    }
    public UnauthorizedException(String message) {
        super(message);
    }
}
