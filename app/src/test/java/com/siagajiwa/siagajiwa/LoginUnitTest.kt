package com.siagajiwa.siagajiwa

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.siagajiwa.siagajiwa.viewmodel.UserUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for login validation logic.
 * These tests run locally without requiring Android device or Supabase connection.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testEmailValidation_validEmail_returnsTrue() {
        // Valid email formats
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.co.id",
            "first+last@test.org",
            "admin@supabase.io"
        )

        validEmails.forEach { email ->
            assertTrue(
                "Email '$email' should be valid",
                isValidEmail(email)
            )
        }
    }

    @Test
    fun testEmailValidation_invalidEmail_returnsFalse() {
        // Invalid email formats
        val invalidEmails = listOf(
            "",                     // Empty
            "notanemail",           // No @
            "@example.com",         // No username
            "user@",                // No domain
            "user @example.com",    // Space in email
            "user@.com",            // Invalid domain
            "user..name@test.com"   // Double dots
        )

        invalidEmails.forEach { email ->
            assertFalse(
                "Email '$email' should be invalid",
                isValidEmail(email)
            )
        }
    }

    @Test
    fun testPasswordValidation_strongPassword_returnsTrue() {
        val strongPasswords = listOf(
            "Password123!",
            "SecurePass@2024",
            "MyP@ssw0rd",
            "Test1234!@#"
        )

        strongPasswords.forEach { password ->
            assertTrue(
                "Password should be strong",
                isStrongPassword(password)
            )
        }
    }

    @Test
    fun testPasswordValidation_weakPassword_returnsFalse() {
        val weakPasswords = listOf(
            "",           // Empty
            "123",        // Too short
            "pass",       // Too short
            "12345"       // Short numbers
        )

        weakPasswords.forEach { password ->
            assertFalse(
                "Password '$password' should be weak",
                isStrongPassword(password)
            )
        }
    }

    @Test
    fun testLoginCredentials_emptyEmail_shouldFail() {
        val email = ""
        val password = "ValidPassword123!"

        assertFalse("Login should fail with empty email", isValidLoginInput(email, password))
    }

    @Test
    fun testLoginCredentials_emptyPassword_shouldFail() {
        val email = "test@example.com"
        val password = ""

        assertFalse("Login should fail with empty password", isValidLoginInput(email, password))
    }

    @Test
    fun testLoginCredentials_bothEmpty_shouldFail() {
        val email = ""
        val password = ""

        assertFalse("Login should fail with empty credentials", isValidLoginInput(email, password))
    }

    @Test
    fun testLoginCredentials_validInputs_shouldPass() {
        val email = "test@example.com"
        val password = "ValidPassword123!"

        assertTrue("Login should pass with valid inputs", isValidLoginInput(email, password))
    }

    @Test
    fun testLoginCredentials_invalidEmail_shouldFail() {
        val email = "not-an-email"
        val password = "ValidPassword123!"

        assertFalse("Login should fail with invalid email format", isValidLoginInput(email, password))
    }

    @Test
    fun testLoginCredentials_weakPassword_shouldFail() {
        val email = "test@example.com"
        val password = "123"

        assertFalse("Login should fail with weak password", isValidLoginInput(email, password))
    }

    @Test
    fun testUserUiState_initialState_isCorrect() {
        val initialState = UserUiState()

        assertNull("Initial user should be null", initialState.user)
        assertFalse("Initial loading state should be false", initialState.isLoading)
        assertNull("Initial error should be null", initialState.error)
        assertFalse("Initial login state should be false", initialState.isLoggedIn)
    }

    @Test
    fun testUserUiState_withLoadingTrue_isCorrect() {
        val loadingState = UserUiState(isLoading = true)

        assertTrue("Loading state should be true", loadingState.isLoading)
        assertNull("User should be null during loading", loadingState.user)
        assertFalse("Should not be logged in during loading", loadingState.isLoggedIn)
    }

    @Test
    fun testUserUiState_withError_isCorrect() {
        val errorMessage = "Invalid credentials"
        val errorState = UserUiState(error = errorMessage)

        assertEquals("Error message should match", errorMessage, errorState.error)
        assertFalse("Should not be logged in on error", errorState.isLoggedIn)
        assertNull("User should be null on error", errorState.user)
    }

    @Test
    fun testUserUiState_withLoggedIn_isCorrect() {
        val loggedInState = UserUiState(isLoggedIn = true)

        assertTrue("Should be logged in", loggedInState.isLoggedIn)
        assertFalse("Should not be loading", loggedInState.isLoading)
        assertNull("Error should be null on success", loggedInState.error)
    }

    @Test
    fun testEmailTrimming_removesWhitespace() {
        val emailsWithSpaces = mapOf(
            " test@example.com" to "test@example.com",
            "test@example.com " to "test@example.com",
            " test@example.com " to "test@example.com",
            "  test@example.com  " to "test@example.com"
        )

        emailsWithSpaces.forEach { (input, expected) ->
            assertEquals(
                "Email should be trimmed correctly",
                expected,
                input.trim()
            )
        }
    }

    @Test
    fun testPasswordRequirements_minimumLength() {
        val minLength = 6

        assertTrue("6-char password should pass", "Pass12".length >= minLength)
        assertTrue("8-char password should pass", "Password".length >= minLength)
        assertFalse("5-char password should fail", "Pass1".length >= minLength)
        assertFalse("3-char password should fail", "123".length >= minLength)
    }

    // Helper functions for validation (these would typically be in a separate validator class)
    private fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        if (!email.contains("@")) return false
        if (!email.contains(".")) return false
        if (email.startsWith("@")) return false
        if (email.endsWith("@")) return false
        if (email.contains(" ")) return false
        if (email.contains("..")) return false
        if (email.contains("@.")) return false

        val parts = email.split("@")
        if (parts.size != 2) return false
        if (parts[0].isEmpty() || parts[1].isEmpty()) return false

        return true
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun isValidLoginInput(email: String, password: String): Boolean {
        return isValidEmail(email) && isStrongPassword(password)
    }
}
