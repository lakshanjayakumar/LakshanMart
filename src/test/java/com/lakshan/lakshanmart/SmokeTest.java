package com.lakshan.lakshanmart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic smoke test to verify test harness and JUnit 5 execution.
 */
class SmokeTest {

    @Test
    @DisplayName("Verify testing framework is configured and functional")
    void testFrameworkInitialization() {
        assertTrue(true, "Testing framework initialized successfully");
    }
}
