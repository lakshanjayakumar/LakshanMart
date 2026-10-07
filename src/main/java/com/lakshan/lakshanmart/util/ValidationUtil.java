package com.lakshan.lakshanmart.util;

import com.lakshan.lakshanmart.exception.ValidationException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility for input validation across services and servlets.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private ValidationUtil() {
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required and cannot be blank.");
        }
    }

    public static void validateEmail(String email) {
        requireNonBlank(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    public static void validatePassword(String password) {
        requireNonBlank(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
    }

    public static void validatePositivePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be a positive decimal greater than 0.");
        }
    }

    public static void validateNonNegativeQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new ValidationException("Stock quantity must be non-negative (0 or greater).");
        }
    }

    public static void requirePositiveId(Long id, String entityName) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid " + entityName + " ID: " + id);
        }
    }
}
