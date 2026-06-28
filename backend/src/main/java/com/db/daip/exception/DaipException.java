package com.db.daip.exception;

/**
 * Base runtime exception for DAIP business logic errors.
 */
public class DaipException extends RuntimeException {

    public DaipException(String message) {
        super(message);
    }

    public DaipException(String message, Throwable cause) {
        super(message, cause);
    }
}
