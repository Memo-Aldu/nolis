package com.nolis.productsearch.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class TokenUnauthorizedToScopeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7L;
    private final String scope;
    public TokenUnauthorizedToScopeException(String message, Exception cause, String scope) {
        super(message, cause);
        this.scope = scope;
    }
    public TokenUnauthorizedToScopeException(String message, String scope) {
        super(message);
        this.scope = scope;
    }

    public TokenUnauthorizedToScopeException(String message) {
        super(message);
        scope = null;
    }
}
