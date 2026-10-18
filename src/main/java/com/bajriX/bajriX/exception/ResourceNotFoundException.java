package com.bajriX.bajriX.exception;

/**
 * Thrown when a requested resource is not found (maps to HTTP 404).
 */
public class ResourceNotFoundException extends BajriXException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
