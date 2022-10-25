package com.nolis.authenticationserver.exception;

import java.io.Serial;

public class TokenAuthenticationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4L;

    public TokenAuthenticationException(String message) {
        super(message);
    }
    public TokenAuthenticationException(String message, Exception exception) {
        super(message, exception);
    }
}
