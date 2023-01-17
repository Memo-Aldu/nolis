package com.nolis.commondata.exception;

import java.io.Serial;

public class BadRequestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BadRequestException(String message, Exception exception) {
        super(message, exception);
    }
    public BadRequestException(String message) {
        super(message);
    }
}
