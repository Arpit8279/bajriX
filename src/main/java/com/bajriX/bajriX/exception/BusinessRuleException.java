package com.bajriX.bajriX.exception;

/**
 * Thrown when a business rule is violated (maps to HTTP 400).
 * Examples: MOQ exceeds stock, seller not approved, etc.
 */
public class BusinessRuleException extends BajriXException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
