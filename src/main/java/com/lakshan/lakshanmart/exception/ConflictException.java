package com.lakshan.lakshanmart.exception;

/**
 * Thrown when an entity conflict occurs (e.g. email already registered). Maps to HTTP 409 Conflict.
 */
public class ConflictException extends AppException {

    public ConflictException(String message) {
        super(409, "CONFLICT", message);
    }
}
