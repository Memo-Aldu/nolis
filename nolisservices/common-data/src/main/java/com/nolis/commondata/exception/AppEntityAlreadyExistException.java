package com.nolis.commondata.exception;

import java.io.Serial;

public class AppEntityAlreadyExistException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2L;

    public AppEntityAlreadyExistException(String message, Exception exception) {
        super(message, exception);
    }
    public AppEntityAlreadyExistException(String message) {
        super(message);
    }
}
