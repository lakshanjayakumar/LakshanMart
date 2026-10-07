package com.lakshan.lakshanmart.exception;

/**
 * Thrown when client input fails domain validation rules. Maps to HTTP 400 Bad Request.
 */
public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(400, "VALIDATION_ERROR", message);
    }
}
