package com.lakshan.lakshanmart.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    @DisplayName("Hash password produces valid BCrypt hash format")
    void testHashPassword() {
        String plain = "SecretPassword123";
        String hash = PasswordUtil.hashPassword(plain);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$10$") || hash.startsWith("$2b$10$"), "Should be standard BCrypt hash");
        assertNotEquals(plain, hash, "Hash must never match plaintext");
    }

    @Test
    @DisplayName("Verify password correctly matches valid hash")
    void testCheckPasswordSuccess() {
        String plain = "MyP@ssw0rd!";
        String hash = PasswordUtil.hashPassword(plain);

        assertTrue(PasswordUtil.checkPassword(plain, hash));
    }

    @Test
    @DisplayName("Verify password rejects incorrect candidate")
    void testCheckPasswordFailure() {
        String plain = "MyP@ssw0rd!";
        String hash = PasswordUtil.hashPassword(plain);

        assertFalse(PasswordUtil.checkPassword("WrongPassword", hash));
        assertFalse(PasswordUtil.checkPassword("", hash));
        assertFalse(PasswordUtil.checkPassword(null, hash));
    }
}
