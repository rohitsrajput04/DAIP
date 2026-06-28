package com.db.daip.exception;

/**
 * Thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends DaipException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
