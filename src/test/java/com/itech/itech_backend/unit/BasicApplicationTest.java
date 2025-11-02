package com.itech.itech_backend.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic Application Tests
 * Simple tests that don't require full application context
 */
@ActiveProfiles("test")
@DisplayName("Basic Application Unit Tests")
public class BasicApplicationTest {

    @Test
    @DisplayName("Should pass basic assertion test")
    void testBasicAssertion() {
        // Basic test to ensure test framework is working
        assertTrue(true, "Basic assertion should pass");
        assertNotNull("Test string", "String should not be null");
        assertEquals(2, 1 + 1, "Math should work correctly");
    }

    @Test
    @DisplayName("Should test string operations")
    void testStringOperations() {
        String testString = "iTech Backend";
        
        assertFalse(testString.isEmpty(), "Test string should not be empty");
        assertTrue(testString.contains("iTech"), "String should contain iTech");
        assertEquals("ITECH BACKEND", testString.toUpperCase(), "Uppercase conversion should work");
    }

    @Test
    @DisplayName("Should test basic Java features")
    void testJavaFeatures() {
        // Test Java 21 features availability
        var list = java.util.List.of("item1", "item2", "item3");
        
        assertFalse(list.isEmpty(), "List should not be empty");
        assertEquals(3, list.size(), "List should have 3 items");
        assertTrue(list.contains("item1"), "List should contain item1");
    }
    
    @Test
    @DisplayName("Should handle exceptions correctly")
    void testExceptionHandling() {
        // Test exception handling
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Test exception");
        }, "Should throw IllegalArgumentException");
        
        assertDoesNotThrow(() -> {
            String result = "No exception here";
            return result;
        }, "Should not throw any exception");
    }
}
