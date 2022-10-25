package com.nolis.authenticationserver.exception;

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

public class AppAuthenticationException extends AuthenticationException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AppAuthenticationException(String message, Exception exception) {
        super(message, exception);
    }
    public AppAuthenticationException(String message) {
        super(message);
    }
}
