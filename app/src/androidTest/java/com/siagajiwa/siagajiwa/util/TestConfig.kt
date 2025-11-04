package com.siagajiwa.siagajiwa.util

/**
 * Configuration for integration tests.
 *
 * IMPORTANT: Update these values with your test credentials before running tests.
 *
 * Test User Setup:
 * 1. Go to your Supabase project dashboard
 * 2. Navigate to Authentication > Users
 * 3. Create a test user or use existing credentials
 * 4. Update the values below
 *
 * Security Note:
 * - DO NOT commit real user credentials to version control
 * - Use environment variables or local.properties for sensitive data
 * - Consider using different test users for different test scenarios
 */
object TestConfig {
    /**
     * Valid test user credentials - used for successful login tests
     */
    const val TEST_USER_EMAIL = "test@example.com"
    const val TEST_USER_PASSWORD = "TestPassword123!"
    const val TEST_USER_FULL_NAME = "Test User"

    /**
     * Invalid credentials - used for negative test cases
     */
    const val INVALID_EMAIL = "nonexistent@example.com"
    const val INVALID_PASSWORD = "wrongpassword"

    /**
     * Malformed data - used for validation tests
     */
    const val MALFORMED_EMAIL = "not-an-email"
    const val WEAK_PASSWORD = "123"

    /**
     * Test delays (in milliseconds)
     */
    const val NETWORK_TIMEOUT = 10000L
    const val SHORT_DELAY = 1000L

    /**
     * Test flags
     */
    const val ENABLE_CLEANUP = true
    const val LOG_VERBOSE = true
}
