package com.nolis.commondata.exception;

import java.io.Serial;

public class ServerErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ServerErrorException(String message, Exception exception) {
        super(message, exception);
    }

    public ServerErrorException(String message) {
        super(message);
    }
}
