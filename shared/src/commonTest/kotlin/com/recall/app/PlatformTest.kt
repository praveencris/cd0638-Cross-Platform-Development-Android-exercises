package com.recall.app

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for Platform and greet() function
 */
class PlatformTest {

    /**
     * Test that greet() function returns a string containing "Hello"
     */
    @Test
    fun testGreetContainsHello() {
        val greeting = greet()
        assertTrue(
            greeting.contains("Hello"),
            "Greeting should contain 'Hello', but was: $greeting"
        )
    }

    /**
     * Test that greet() function returns a string containing the current year (2026)
     */
    @Test
    fun testGreetContainsCurrentYear() {
        val greeting = greet()
        assertTrue(
            greeting.contains("2026"),
            "Greeting should contain '2026', but was: $greeting"
        )
    }
}
