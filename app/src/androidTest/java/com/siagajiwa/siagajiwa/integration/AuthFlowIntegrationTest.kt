package com.siagajiwa.siagajiwa.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.siagajiwa.siagajiwa.data.repository.UserRepository
import com.siagajiwa.siagajiwa.util.TestConfig
import com.siagajiwa.siagajiwa.util.TestUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Advanced integration tests for complete authentication flows.
 *
 * These tests verify end-to-end authentication scenarios including:
 * - Complete login/logout cycles
 * - User profile retrieval after login
 * - Session persistence
 * - Error handling in realistic scenarios
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AuthFlowIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var userRepository: UserRepository

    @Before
    fun setup() = runTest {
        userRepository = UserRepository()
        TestUtils.ensureLoggedOut(userRepository)
        TestUtils.log("AuthFlowTest", "Setup completed")
    }

    @After
    fun tearDown() = runTest {
        TestUtils.ensureLoggedOut(userRepository)
        TestUtils.log("AuthFlowTest", "Teardown completed")
    }

    @Test
    fun testCompleteLoginFlow() = runTest {
        TestUtils.log("AuthFlowTest", "Starting complete login flow test")

        // Step 1: Verify initial state (logged out)
        val initialUser = userRepository.getCurrentUser()
        assertNull("User should be logged out initially", initialUser)

        // Step 2: Perform login
        val userId = TestUtils.assertLoginSuccess(
            userRepository,
            TestConfig.TEST_USER_EMAIL,
            TestConfig.TEST_USER_PASSWORD
        )

        // Step 3: Verify session is active
        val currentUser = userRepository.getCurrentUser()
        assertNotNull("User should be logged in", currentUser)
        assertEquals("User IDs should match", userId, currentUser?.id)

        TestUtils.log("AuthFlowTest", "Complete login flow test passed")
    }

    @Test
    fun testCompleteLoginLogoutCycle() = runTest {
        TestUtils.log("AuthFlowTest", "Starting login-logout cycle test")

        // Login
        TestUtils.assertLoginSuccess(
            userRepository,
            TestConfig.TEST_USER_EMAIL,
            TestConfig.TEST_USER_PASSWORD
        )

        // Verify logged in
        assertNotNull("User should be logged in", userRepository.getCurrentUser())

        // Logout
        val logoutResult = userRepository.signOut()
        assertTrue("Logout should succeed", logoutResult.isSuccess)

        // Verify logged out
        assertNull("User should be logged out", userRepository.getCurrentUser())

        TestUtils.log("AuthFlowTest", "Login-logout cycle test passed")
    }

    @Test
    fun testLoginAndFetchUserProfile() = runTest {
        TestUtils.log("AuthFlowTest", "Starting login and fetch profile test")

        // Login
        val userId = TestUtils.assertLoginSuccess(
            userRepository,
            TestConfig.TEST_USER_EMAIL,
            TestConfig.TEST_USER_PASSWORD
        )

        // Fetch user profile
        val profileResult = userRepository.getUserProfile(userId)

        // Verify profile was fetched
        assertTrue("Profile fetch should succeed", profileResult.isSuccess)

        profileResult.onSuccess { user ->
            assertNotNull("User profile should not be null", user)
            assertEquals("User IDs should match", userId, user.id)
            assertEquals("Email should match", TestConfig.TEST_USER_EMAIL, user.email)
            assertNotNull("Full name should not be null", user.fullName)
        }

        TestUtils.log("AuthFlowTest", "Login and fetch profile test passed")
    }

    @Test
    fun testMultipleLoginAttempts() = runTest {
        TestUtils.log("AuthFlowTest", "Starting multiple login attempts test")

        // First successful login
        TestUtils.assertLoginSuccess(
            userRepository,
            TestConfig.TEST_USER_EMAIL,
            TestConfig.TEST_USER_PASSWORD
        )

        // Logout
        userRepository.signOut()

        // Failed login attempt
        TestUtils.assertLoginFailure(
            userRepository,
            TestConfig.INVALID_EMAIL,
            TestConfig.INVALID_PASSWORD
        )

        // Another successful login
        TestUtils.assertLoginSuccess(
            userRepository,
            TestConfig.TEST_USER_EMAIL,
            TestConfig.TEST_USER_PASSWORD
        )

        TestUtils.log("AuthFlowTest", "Multiple login attempts test passed")
    }

    @Test
    fun testSessionValidityAfterLogin() = runTest {
        TestUtils.log("AuthFlowTest", "Starting session validity test")

        // Login
        val userId = TestUtils.assertLoginSuccess(
            userRepository,
            TestConfig.TEST_USER_EMAIL,
            TestConfig.TEST_USER_PASSWORD
        )

        // Wait briefly and check session is still valid
        val sessionValid = TestUtils.waitForCondition(
            timeoutMs = 2000,
            checkIntervalMs = 500
        ) {
            val user = userRepository.getCurrentUser()
            user != null && user.id == userId
        }

        assertTrue("Session should remain valid", sessionValid)

        TestUtils.log("AuthFlowTest", "Session validity test passed")
    }

    @Test
    fun testErrorHandlingOnInvalidCredentials() = runTest {
        TestUtils.log("AuthFlowTest", "Starting error handling test")

        // Test with invalid email
        TestUtils.assertLoginFailure(
            userRepository,
            TestConfig.INVALID_EMAIL,
            TestConfig.TEST_USER_PASSWORD
        )

        // Test with invalid password
        TestUtils.assertLoginFailure(
            userRepository,
            TestConfig.TEST_USER_EMAIL,
            TestConfig.INVALID_PASSWORD
        )

        // Test with malformed email
        TestUtils.assertLoginFailure(
            userRepository,
            TestConfig.MALFORMED_EMAIL,
            TestConfig.TEST_USER_PASSWORD
        )

        // Verify no user is logged in
        assertNull("No user should be logged in", userRepository.getCurrentUser())

        TestUtils.log("AuthFlowTest", "Error handling test passed")
    }

    @Test
    fun testLoginWithEmptyCredentials() = runTest {
        TestUtils.log("AuthFlowTest", "Starting empty credentials test")

        // Test with empty email and password
        TestUtils.assertLoginFailure(userRepository, "", "")

        // Test with empty email only
        TestUtils.assertLoginFailure(userRepository, "", TestConfig.TEST_USER_PASSWORD)

        // Test with empty password only
        TestUtils.assertLoginFailure(userRepository, TestConfig.TEST_USER_EMAIL, "")

        TestUtils.log("AuthFlowTest", "Empty credentials test passed")
    }

    @Test
    fun testUserProfileNotFoundForInvalidUserId() = runTest {
        TestUtils.log("AuthFlowTest", "Starting invalid user ID test")

        // Login first
        TestUtils.assertLoginSuccess(
            userRepository,
            TestConfig.TEST_USER_EMAIL,
            TestConfig.TEST_USER_PASSWORD
        )

        // Try to fetch profile with invalid user ID
        val invalidUserId = "00000000-0000-0000-0000-000000000000"
        val result = userRepository.getUserProfile(invalidUserId)

        // Should fail to find the user
        assertTrue("Profile fetch should fail for invalid user ID", result.isFailure)

        TestUtils.log("AuthFlowTest", "Invalid user ID test passed")
    }

    @Test
    fun testLogoutWhenNotLoggedIn() = runTest {
        TestUtils.log("AuthFlowTest", "Starting logout when not logged in test")

        // Ensure logged out
        TestUtils.ensureLoggedOut(userRepository)

        // Try to logout again
        val result = userRepository.signOut()

        // Should still succeed (idempotent operation)
        assertTrue("Logout should succeed even when not logged in", result.isSuccess)

        TestUtils.log("AuthFlowTest", "Logout when not logged in test passed")
    }

    @Test
    fun testConsecutiveLogins() = runTest {
        TestUtils.log("AuthFlowTest", "Starting consecutive logins test")

        // Login 3 times consecutively
        repeat(3) { iteration ->
            TestUtils.log("AuthFlowTest", "Login iteration ${iteration + 1}")

            TestUtils.assertLoginSuccess(
                userRepository,
                TestConfig.TEST_USER_EMAIL,
                TestConfig.TEST_USER_PASSWORD
            )

            val currentUser = userRepository.getCurrentUser()
            assertNotNull("User should be logged in after iteration ${iteration + 1}", currentUser)

            userRepository.signOut()
        }

        TestUtils.log("AuthFlowTest", "Consecutive logins test passed")
    }
}
