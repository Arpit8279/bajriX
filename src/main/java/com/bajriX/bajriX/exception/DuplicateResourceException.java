package com.bajriX.bajriX.exception;

/**
 * Thrown when attempting to create a duplicate resource (maps to HTTP 409).
 * Example: seller tries to list a product they already sell.
 */
public class DuplicateResourceException extends BajriXException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
