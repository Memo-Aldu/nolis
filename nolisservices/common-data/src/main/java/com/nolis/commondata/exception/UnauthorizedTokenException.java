package com.nolis.authenticationserver.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class UnauthorizedTokenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7L;
    private final String scope;
    public UnauthorizedTokenException(String message, Exception cause, String scope) {
        super(message, cause);
        this.scope = scope;
    }
    public UnauthorizedTokenException(String message, String scope) {
        super(message);
        this.scope = scope;
    }

    public UnauthorizedTokenException(String message) {
        super(message);
        scope = null;
    }
}
