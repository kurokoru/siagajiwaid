package com.siagajiwa.siagajiwaid.integration

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.siagajiwa.siagajiwaid.data.repository.MediaRepository
import com.siagajiwa.siagajiwaid.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for MediaRepository functionality.
 *
 * IMPORTANT: These tests require:
 * 1. Active internet connection
 * 2. Valid Supabase project configuration
 * 3. Test user credentials (vahyo@yopmail.com / 123456)
 * 4. Data in perawatan_media table
 *
 * This test logs in with the specified user and fetches patient care media
 * from the perawatan_media table, displaying results in the test output.
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MediaIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var userRepository: UserRepository
    private lateinit var mediaRepository: MediaRepository

    // Test credentials
    private val TEST_EMAIL = "vahyo@yopmail.com"
    private val TEST_PASSWORD = "123456"

    private val TAG = "MediaIntegrationTest"

    @Before
    fun setup() {
        userRepository = UserRepository()
        mediaRepository = MediaRepository()
    }

    @After
    fun tearDown() = runTest {
        // Clean up: Sign out after each test
        try {
            userRepository.signOut()
            Log.d(TAG, "✓ User signed out successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out: ${e.message}")
        }
    }

    @Test
    fun testGetPatientCareMediaWithAuthentication() = runTest {
        Log.d(TAG, "========================================")
        Log.d(TAG, "Starting Media Integration Test")
        Log.d(TAG, "========================================")

        // Step 1: Login with test credentials
        Log.d(TAG, "\n--- Step 1: Authentication ---")
        Log.d(TAG, "Attempting to login with: $TEST_EMAIL")

        val loginResult = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)

        // Check result and log details before asserting
        if (loginResult.isSuccess) {
            loginResult.onSuccess { userInfo ->
                Log.d(TAG, "✓ Login successful!")
                Log.d(TAG, "  User ID: ${userInfo.id}")
                Log.d(TAG, "  Email: ${userInfo.email}")
                assertNotNull("User info should not be null", userInfo)
                assertEquals("Email should match", TEST_EMAIL, userInfo.email)
            }
        } else {
            loginResult.onFailure { exception ->
                Log.e(TAG, "✗ Login failed!")
                Log.e(TAG, "  Exception type: ${exception::class.simpleName}")
                Log.e(TAG, "  Exception message: ${exception.message}")
                Log.e(TAG, "  Stack trace:", exception)
                exception.printStackTrace()
                fail("Login failed: ${exception.message}")
            }
        }

        // Verify user is logged in
        val currentUser = userRepository.getCurrentUser()
        assertNotNull("Current user should not be null after login", currentUser)
        Log.d(TAG, "✓ User session verified")

        // Step 2: Fetch Patient Care Media
        Log.d(TAG, "\n--- Step 2: Fetching Patient Care Media ---")
        Log.d(TAG, "Calling getPatientCareMedia()...")

        val mediaResult = mediaRepository.getPatientCareMedia()

        assertTrue("Media fetch should succeed", mediaResult.isSuccess)

        mediaResult.onSuccess { mediaList ->
            Log.d(TAG, "✓ Media fetch successful!")
            Log.d(TAG, "  Total items: ${mediaList.size}")

            // Verify we got data
            assertNotNull("Media list should not be null", mediaList)

            if (mediaList.isEmpty()) {
                Log.w(TAG, "⚠ Warning: No media items found in perawatan_media table")
            } else {
                Log.d(TAG, "\n--- Step 3: Media Content Details ---")
                Log.d(TAG, "┌─────────────────────────────────────────────────────────────────────────────┐")
                Log.d(TAG, "│                     PATIENT CARE MEDIA TABLE RESULTS                        │")
                Log.d(TAG, "├─────────────────────────────────────────────────────────────────────────────┤")

                mediaList.forEachIndexed { index, media ->
                    Log.d(TAG, "│ Item ${index + 1}/${mediaList.size}")
                    Log.d(TAG, "│   ID: ${media.id}")
                    Log.d(TAG, "│   Order: ${media.order}")
                    Log.d(TAG, "│   Link: ${media.link}")
                    Log.d(TAG, "│   Created At: ${media.createdAt}")
                    Log.d(TAG, "├─────────────────────────────────────────────────────────────────────────────┤")

                    // Verify each media item has required fields
                    assertNotNull("Media ID should not be null", media.id)
                    assertNotNull("Media link should not be null", media.link)
                    assertFalse("Media link should not be empty", media.link.isEmpty())
                    assertTrue("Media order should be non-negative", media.order >= 0)
                }

                Log.d(TAG, "└─────────────────────────────────────────────────────────────────────────────┘")

                // Verify media is ordered correctly
                val orders = mediaList.map { it.order }
                val sortedOrders = orders.sorted()
                assertEquals(
                    "Media should be ordered by 'order' field in ascending order",
                    sortedOrders,
                    orders
                )
                Log.d(TAG, "✓ Media items are correctly ordered: $orders")
            }
        }

        mediaResult.onFailure { exception ->
            Log.e(TAG, "✗ Media fetch failed: ${exception.message}")
            exception.printStackTrace()
            fail("Media fetch failed: ${exception.message}")
        }

        Log.d(TAG, "\n========================================")
        Log.d(TAG, "Test Completed Successfully!")
        Log.d(TAG, "========================================")
    }

    @Test
    fun testGetPatientCareMediaWithoutAuthentication() = runTest {
        Log.d(TAG, "\n--- Testing Media Access Without Authentication ---")

        // Ensure user is signed out
        userRepository.signOut()

        // Try to fetch media without being logged in
        val mediaResult = mediaRepository.getPatientCareMedia()

        // Note: Depending on your Supabase RLS policies, this might succeed or fail
        // If your table has public read access, it will succeed
        // If it requires authentication, it will fail

        if (mediaResult.isSuccess) {
            mediaResult.onSuccess { mediaList ->
                Log.d(TAG, "✓ Media fetch succeeded without authentication (public access)")
                Log.d(TAG, "  Total items: ${mediaList.size}")
            }
        } else {
            mediaResult.onFailure { exception ->
                Log.d(TAG, "✓ Media fetch failed without authentication (protected by RLS)")
                Log.d(TAG, "  Error: ${exception.message}")
            }
        }
    }

    @Test
    fun testGetAllMediaTypes() = runTest {
        Log.d(TAG, "\n--- Testing All Media Types ---")

        // Login first
        val loginResult = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Login should succeed", loginResult.isSuccess)

        Log.d(TAG, "✓ Logged in successfully")

        // Test Patient Care Media
        Log.d(TAG, "\n1. Patient Care Media (perawatan_media):")
        val patientCareResult = mediaRepository.getPatientCareMedia()
        if (patientCareResult.isSuccess) {
            patientCareResult.onSuccess { media ->
                Log.d(TAG, "   ✓ Found ${media.size} items")
            }
        } else {
            Log.e(TAG, "   ✗ Failed to fetch: ${patientCareResult.exceptionOrNull()?.message}")
        }

        // Test Stress Media
        Log.d(TAG, "\n2. Stress Media (stress_media):")
        val stressMediaResult = mediaRepository.getStressMedia()
        if (stressMediaResult.isSuccess) {
            stressMediaResult.onSuccess { media ->
                Log.d(TAG, "   ✓ Found ${media.size} items")
            }
        } else {
            Log.e(TAG, "   ✗ Failed to fetch: ${stressMediaResult.exceptionOrNull()?.message}")
        }

        // Test Schizophrenia Media
        Log.d(TAG, "\n3. Schizophrenia Media (skizo_media):")
        val schizoMediaResult = mediaRepository.getSchizophreniaMedia()
        if (schizoMediaResult.isSuccess) {
            schizoMediaResult.onSuccess { media ->
                Log.d(TAG, "   ✓ Found ${media.size} items")
            }
        } else {
            Log.e(TAG, "   ✗ Failed to fetch: ${schizoMediaResult.exceptionOrNull()?.message}")
        }

        Log.d(TAG, "\n✓ All media types tested")
    }

    @Test
    fun testMediaDataIntegrity() = runTest {
        Log.d(TAG, "\n--- Testing Media Data Integrity ---")

        // Login
        val loginResult = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Login should succeed", loginResult.isSuccess)

        // Fetch patient care media
        val mediaResult = mediaRepository.getPatientCareMedia()
        assertTrue("Media fetch should succeed", mediaResult.isSuccess)

        mediaResult.onSuccess { mediaList ->
            Log.d(TAG, "Checking data integrity for ${mediaList.size} items...")

            mediaList.forEach { media ->
                // Check ID is valid
                assertTrue("ID should be positive", media.id > 0)

                // Check link is valid (basic check)
                assertTrue("Link should not be empty", media.link.isNotEmpty())
                assertTrue(
                    "Link should be a valid URL or path",
                    media.link.startsWith("http") || media.link.startsWith("/") || media.link.startsWith("file://")
                )

                // Check created_at is not empty
                assertTrue("Created at should not be empty", media.createdAt.isNotEmpty())

                // Check order is reasonable
                assertTrue("Order should be non-negative", media.order >= 0)

                Log.d(TAG, "  ✓ Media ${media.id} passed integrity checks")
            }

            Log.d(TAG, "✓ All media items passed integrity checks")
        }
    }
}
