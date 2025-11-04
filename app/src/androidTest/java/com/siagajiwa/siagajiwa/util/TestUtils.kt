package com.siagajiwa.siagajiwa.util

import com.siagajiwa.siagajiwa.data.repository.UserRepository
import kotlinx.coroutines.delay
import org.junit.Assert.*

/**
 * Utility functions for integration tests.
 */
object TestUtils {

    /**
     * Ensures the user is logged out before starting a test.
     * Useful for test setup to guarantee a clean state.
     */
    suspend fun ensureLoggedOut(repository: UserRepository) {
        try {
            repository.signOut()
            delay(500) // Small delay to ensure logout completes
        } catch (e: Exception) {
            // Ignore errors if already logged out
        }
    }

    /**
     * Verifies that a login was successful and returns the user ID.
     * Throws an assertion error if login failed.
     */
    suspend fun assertLoginSuccess(
        repository: UserRepository,
        email: String,
        password: String
    ): String {
        val result = repository.signInWithEmail(email, password)
        assertTrue("Login should succeed", result.isSuccess)

        var userId = ""
        result.onSuccess { userInfo ->
            assertNotNull("User info should not be null", userInfo)
            assertEquals("Email should match", email, userInfo.email)
            assertNotNull("User ID should not be null", userInfo.id)
            userId = userInfo.id
        }

        return userId
    }

    /**
     * Verifies that a login failed with an error.
     */
    suspend fun assertLoginFailure(
        repository: UserRepository,
        email: String,
        password: String
    ) {
        val result = repository.signInWithEmail(email, password)
        assertTrue("Login should fail", result.isFailure)

        result.onFailure { exception ->
            assertNotNull("Exception should not be null", exception)
            assertNotNull("Error message should not be null", exception.message)
        }

        // Verify user is not logged in
        val currentUser = repository.getCurrentUser()
        assertNull("Current user should be null after failed login", currentUser)
    }

    /**
     * Creates a unique test email to avoid conflicts.
     */
    fun generateUniqueTestEmail(): String {
        val timestamp = System.currentTimeMillis()
        return "test_$timestamp@example.com"
    }

    /**
     * Waits for a condition to be true within a timeout period.
     * Useful for waiting for async operations to complete.
     */
    suspend fun waitForCondition(
        timeoutMs: Long = TestConfig.NETWORK_TIMEOUT,
        checkIntervalMs: Long = 100,
        condition: suspend () -> Boolean
    ): Boolean {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (condition()) {
                return true
            }
            delay(checkIntervalMs)
        }
        return false
    }

    /**
     * Logs test information if verbose logging is enabled.
     */
    fun log(tag: String, message: String) {
        if (TestConfig.LOG_VERBOSE) {
            println("[$tag] $message")
        }
    }

    /**
     * Validates email format (simple validation).
     */
    fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    /**
     * Validates password strength (simple validation).
     */
    fun isStrongPassword(password: String): Boolean {
        return password.length >= 6
    }
}
