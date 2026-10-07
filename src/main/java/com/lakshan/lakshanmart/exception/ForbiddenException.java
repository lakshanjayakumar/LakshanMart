package com.lakshan.lakshanmart.exception;

/**
 * Thrown when an authenticated user lacks permissions for an operation. Maps to HTTP 403 Forbidden.
 */
public class ForbiddenException extends AppException {

    public ForbiddenException(String message) {
        super(403, "FORBIDDEN", message);
    }
}
