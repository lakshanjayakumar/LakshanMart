package com.lakshan.lakshanmart.exception;

/**
 * Thrown when credentials fail or authentication is missing. Maps to HTTP 401 Unauthorized.
 */
public class AuthenticationException extends AppException {

    public AuthenticationException(String message) {
        super(401, "AUTHENTICATION_FAILED", message);
    }
}
