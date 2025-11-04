package com.siagajiwa.siagajiwa.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.siagajiwa.siagajiwa.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for Supabase login functionality.
 *
 * IMPORTANT: These tests require:
 * 1. Active internet connection
 * 2. Valid Supabase project configuration
 * 3. Test user credentials set up in Supabase
 *
 * Before running these tests:
 * - Create a test user in your Supabase project
 * - Update TEST_EMAIL and TEST_PASSWORD with valid credentials
 * - Ensure your Supabase project is accessible
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class LoginIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var userRepository: UserRepository

    // Test credentials - UPDATE THESE with valid test user credentials
    private val TEST_EMAIL = "test@example.com"
    private val TEST_PASSWORD = "TestPassword123!"
    private val INVALID_EMAIL = "invalid@example.com"
    private val INVALID_PASSWORD = "wrongpassword"

    @Before
    fun setup() {
        userRepository = UserRepository()
    }

    @After
    fun tearDown() = runTest {
        // Clean up: Sign out after each test
        try {
            userRepository.signOut()
        } catch (e: Exception) {
            // Ignore sign out errors in cleanup
        }
    }

    @Test
    fun testSuccessfulLogin() = runTest {
        // Given: Valid credentials
        val email = TEST_EMAIL
        val password = TEST_PASSWORD

        // When: Attempting to sign in
        val result = userRepository.signInWithEmail(email, password)

        // Then: Login should succeed
        assertTrue("Login should succeed with valid credentials", result.isSuccess)

        result.onSuccess { userInfo ->
            assertNotNull("User info should not be null", userInfo)
            assertEquals("Email should match", email, userInfo.email)
            assertNotNull("User ID should not be null", userInfo.id)
        }

        // Verify user is logged in
        val currentUser = userRepository.getCurrentUser()
        assertNotNull("Current user should not be null after login", currentUser)
        assertEquals("Logged in user email should match", email, currentUser?.email)
    }

    @Test
    fun testLoginWithInvalidEmail() = runTest {
        // Given: Invalid email
        val email = INVALID_EMAIL
        val password = TEST_PASSWORD

        // When: Attempting to sign in
        val result = userRepository.signInWithEmail(email, password)

        // Then: Login should fail
        assertTrue("Login should fail with invalid email", result.isFailure)

        result.onFailure { exception ->
            assertNotNull("Exception should not be null", exception)
            assertNotNull("Error message should not be null", exception.message)
        }

        // Verify user is not logged in
        val currentUser = userRepository.getCurrentUser()
        assertNull("Current user should be null after failed login", currentUser)
    }

    @Test
    fun testLoginWithInvalidPassword() = runTest {
        // Given: Valid email but invalid password
        val email = TEST_EMAIL
        val password = INVALID_PASSWORD

        // When: Attempting to sign in
        val result = userRepository.signInWithEmail(email, password)

        // Then: Login should fail
        assertTrue("Login should fail with invalid password", result.isFailure)

        result.onFailure { exception ->
            assertNotNull("Exception should not be null", exception)
            assertNotNull("Error message should not be null", exception.message)
        }

        // Verify user is not logged in
        val currentUser = userRepository.getCurrentUser()
        assertNull("Current user should be null after failed login", currentUser)
    }

    @Test
    fun testLoginWithEmptyCredentials() = runTest {
        // Given: Empty credentials
        val email = ""
        val password = ""

        // When: Attempting to sign in
        val result = userRepository.signInWithEmail(email, password)

        // Then: Login should fail
        assertTrue("Login should fail with empty credentials", result.isFailure)

        result.onFailure { exception ->
            assertNotNull("Exception should not be null", exception)
        }
    }

    @Test
    fun testLoginWithMalformedEmail() = runTest {
        // Given: Malformed email
        val email = "not-an-email"
        val password = TEST_PASSWORD

        // When: Attempting to sign in
        val result = userRepository.signInWithEmail(email, password)

        // Then: Login should fail
        assertTrue("Login should fail with malformed email", result.isFailure)

        result.onFailure { exception ->
            assertNotNull("Exception should not be null", exception)
        }
    }

    @Test
    fun testSuccessfulLoginAndLogout() = runTest {
        // Given: Valid credentials and successful login
        val result = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Login should succeed", result.isSuccess)

        // When: Signing out
        val signOutResult = userRepository.signOut()

        // Then: Sign out should succeed
        assertTrue("Sign out should succeed", signOutResult.isSuccess)

        // Verify user is logged out
        val currentUser = userRepository.getCurrentUser()
        assertNull("Current user should be null after logout", currentUser)
    }

    @Test
    fun testGetCurrentUserWhenNotLoggedIn() = runTest {
        // Given: No user is logged in
        userRepository.signOut()

        // When: Getting current user
        val currentUser = userRepository.getCurrentUser()

        // Then: Current user should be null
        assertNull("Current user should be null when not logged in", currentUser)
    }

    @Test
    fun testMultipleSuccessfulLogins() = runTest {
        // Test logging in multiple times with the same account

        // First login
        val result1 = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("First login should succeed", result1.isSuccess)

        // Logout
        userRepository.signOut()

        // Second login
        val result2 = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Second login should succeed", result2.isSuccess)

        // Verify user is logged in
        val currentUser = userRepository.getCurrentUser()
        assertNotNull("Current user should not be null", currentUser)
        assertEquals("Email should match", TEST_EMAIL, currentUser?.email)
    }

    @Test
    fun testLoginStateAfterSuccessfulLogin() = runTest {
        // Given: Successful login
        val result = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Login should succeed", result.isSuccess)

        // Then: User state should be valid
        result.onSuccess { userInfo ->
            // Check all important user info fields
            assertNotNull("User ID should not be null", userInfo.id)
            assertNotNull("Email should not be null", userInfo.email)
            assertEquals("Email should match login email", TEST_EMAIL, userInfo.email)
            assertFalse("User ID should not be empty", userInfo.id.isEmpty())
        }
    }

    @Test
    fun testSessionPersistenceAfterLogin() = runTest {
        // Given: Successful login
        userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)

        // When: Getting current user immediately after login
        val currentUser = userRepository.getCurrentUser()

        // Then: Session should be available
        assertNotNull("Session should persist after login", currentUser)
        assertEquals("User email should be accessible from session", TEST_EMAIL, currentUser?.email)
    }
}
