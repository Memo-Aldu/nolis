package com.nolis.commondata.exception;

import java.io.Serial;

public class AppEntityNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AppEntityNotFoundException(String message, Exception exception) {
        super(message, exception);
    }
    public AppEntityNotFoundException(String message) {
        super(message);
    }
}
