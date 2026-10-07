package com.lakshan.lakshanmart.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying passwords using BCrypt.
 * Strictly adheres to Section 2 & 9 rules: passwords hashed with bcrypt, no plaintext or weak hashes.
 */
public final class PasswordUtil {

    private static final int BCRYPT_WORKLOAD = 10;

    private PasswordUtil() {
    }

    /**
     * Hashes a plaintext password using BCrypt with workload 10.
     *
     * @param plainPassword plaintext password
     * @return bcrypt hash string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_WORKLOAD));
    }

    /**
     * Verifies a candidate plaintext password against a stored BCrypt hash.
     *
     * @param plainPassword candidate password
     * @param hashedPassword stored bcrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Malformed hash
            return false;
        }
    }
}
