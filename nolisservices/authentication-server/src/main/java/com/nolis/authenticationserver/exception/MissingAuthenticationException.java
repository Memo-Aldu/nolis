package com.nolis.authenticationserver.exception;

import java.io.Serial;

public class MissingAuthenticationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5L;

    public MissingAuthenticationException(String message, Exception exception) {
        super(message, exception);
    }

    public MissingAuthenticationException(String message) {
        super(message);
    }
}
