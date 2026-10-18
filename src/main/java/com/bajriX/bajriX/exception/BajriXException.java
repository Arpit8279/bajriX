package com.bajriX.bajriX.exception;

/**
 * Base exception for all BajriX business exceptions.
 */
public class BajriXException extends RuntimeException {

    public BajriXException(String message) {
        super(message);
    }

    public BajriXException(String message, Throwable cause) {
        super(message, cause);
    }
}
