package com.lakshan.lakshanmart.exception;

/**
 * Thrown when a requested resource is not found. Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String message) {
        super(404, "NOT_FOUND", message);
    }
}
