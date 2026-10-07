package com.lakshan.lakshanmart.exception;

/**
 * Thrown when low-level JDBC or database failures occur. Maps to HTTP 500 Internal Server Error.
 */
public class DatabaseException extends AppException {

    public DatabaseException(String message) {
        super(500, "DATABASE_ERROR", message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(500, "DATABASE_ERROR", message, cause);
    }
}
