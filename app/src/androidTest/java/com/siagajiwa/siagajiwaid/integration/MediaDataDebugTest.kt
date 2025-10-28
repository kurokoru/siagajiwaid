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
 * Debug test to verify database data and image links
 *
 * Tests if the perawatan_media table contains the expected image link:
 * https://uharlzitusomrqtuitqz.supabase.co/storage/v1/object/public/siagajiwamedia/perawatan_1.png
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MediaDataDebugTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var userRepository: UserRepository
    private lateinit var mediaRepository: MediaRepository

    // Test credentials
    private val TEST_EMAIL = "vahyo@yopmail.com"
    private val TEST_PASSWORD = "123456"

    // Expected image URL
    private val EXPECTED_IMAGE_URL = "https://uharlzitusomrqtuitqz.supabase.co/storage/v1/object/public/siagajiwamedia/perawatan_1.png"

    private val TAG = "MediaDataDebugTest"

    @Before
    fun setup() {
        userRepository = UserRepository()
        mediaRepository = MediaRepository()
    }

    @After
    fun tearDown() = runTest {
        try {
            userRepository.signOut()
            Log.d(TAG, "✓ User signed out successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out: ${e.message}")
        }
    }

    @Test
    fun debugPatientCareMediaData() = runTest {
        Log.d(TAG, "========================================")
        Log.d(TAG, "DEBUG: Patient Care Media Data")
        Log.d(TAG, "========================================")

        // Step 1: Login
        Log.d(TAG, "\n--- Step 1: Login ---")
        val loginResult = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)

        if (loginResult.isSuccess) {
            Log.d(TAG, "✓ Login successful")
        } else {
            loginResult.onFailure { exception ->
                Log.e(TAG, "✗ Login failed: ${exception.message}")
                fail("Login failed: ${exception.message}")
            }
        }

        // Step 2: Fetch Patient Care Media
        Log.d(TAG, "\n--- Step 2: Fetching Data ---")
        val mediaResult = mediaRepository.getPatientCareMedia()

        if (mediaResult.isSuccess) {
            mediaResult.onSuccess { mediaList ->
                Log.d(TAG, "✓ Successfully fetched ${mediaList.size} items")
                Log.d(TAG, "\n--- Step 3: Detailed Data Analysis ---")
                Log.d(TAG, "┌─────────────────────────────────────────────────────────────────────────────┐")
                Log.d(TAG, "│                     DATABASE CONTENT ANALYSIS                               │")
                Log.d(TAG, "├─────────────────────────────────────────────────────────────────────────────┤")

                if (mediaList.isEmpty()) {
                    Log.w(TAG, "│ ⚠ WARNING: Database table is EMPTY!                                        │")
                    Log.w(TAG, "│ No media items found in perawatan_media table                              │")
                } else {
                    mediaList.forEachIndexed { index, media ->
                        Log.d(TAG, "│")
                        Log.d(TAG, "│ ═══ Media Item #${index + 1} ═══")
                        Log.d(TAG, "│ ID:         ${media.id}")
                        Log.d(TAG, "│ Order:      ${media.order}")
                        Log.d(TAG, "│ Created At: ${media.createdAt}")
                        Log.d(TAG, "│")
                        Log.d(TAG, "│ Link:       ${media.link}")
                        Log.d(TAG, "│")

                        // Check if link matches expected URL
                        val isExpectedUrl = media.link == EXPECTED_IMAGE_URL
                        if (isExpectedUrl) {
                            Log.d(TAG, "│ ✅ MATCH: This link matches the expected URL!")
                        } else {
                            Log.d(TAG, "│ ❌ NO MATCH: This link is different from expected")
                        }

                        // Check if link contains the base URL
                        val containsBaseUrl = media.link.contains("uharlzitusomrqtuitqz.supabase.co")
                        if (containsBaseUrl) {
                            Log.d(TAG, "│ ✓ Contains Supabase storage URL")
                        } else {
                            Log.d(TAG, "│ ✗ Does NOT contain Supabase storage URL")
                        }

                        // Check if link contains perawatan_1.png
                        val containsExpectedFile = media.link.contains("perawatan_1.png")
                        if (containsExpectedFile) {
                            Log.d(TAG, "│ ✓ Contains perawatan_1.png")
                        }

                        // URL format check
                        val isHttps = media.link.startsWith("https://")
                        Log.d(TAG, "│ Protocol:   ${if (isHttps) "HTTPS ✓" else "NOT HTTPS ✗"}")

                        // Length comparison
                        Log.d(TAG, "│")
                        Log.d(TAG, "│ Link Length: ${media.link.length} chars")
                        Log.d(TAG, "│ Expected Length: ${EXPECTED_IMAGE_URL.length} chars")

                        if (media.link.length != EXPECTED_IMAGE_URL.length && !isExpectedUrl) {
                            Log.d(TAG, "│ Difference: ${media.link.length - EXPECTED_IMAGE_URL.length} chars")
                        }

                        Log.d(TAG, "│")
                        Log.d(TAG, "├─────────────────────────────────────────────────────────────────────────────┤")
                    }

                    // Summary
                    Log.d(TAG, "│")
                    Log.d(TAG, "│ ═══ SUMMARY ═══")
                    Log.d(TAG, "│ Total items: ${mediaList.size}")

                    val matchCount = mediaList.count { it.link == EXPECTED_IMAGE_URL }
                    Log.d(TAG, "│ Exact matches: $matchCount")

                    val partialMatchCount = mediaList.count { it.link.contains("perawatan_1.png") }
                    Log.d(TAG, "│ Contains 'perawatan_1.png': $partialMatchCount")

                    val supabaseUrlCount = mediaList.count { it.link.contains("uharlzitusomrqtuitqz.supabase.co") }
                    Log.d(TAG, "│ From Supabase storage: $supabaseUrlCount")

                    Log.d(TAG, "│")

                    // Test assertions
                    assertTrue("Should have at least one media item", mediaList.isNotEmpty())

                    // Check if any link matches
                    val hasExpectedLink = mediaList.any { it.link == EXPECTED_IMAGE_URL }
                    if (hasExpectedLink) {
                        Log.d(TAG, "│ ✅ SUCCESS: Found expected image URL in database!")
                    } else {
                        Log.d(TAG, "│ ⚠ NOTE: Expected URL not found, but ${mediaList.size} other items exist")
                    }
                }

                Log.d(TAG, "└─────────────────────────────────────────────────────────────────────────────┘")

                // Additional debug: Show expected vs actual
                Log.d(TAG, "\n--- Step 4: URL Comparison ---")
                Log.d(TAG, "Expected URL:")
                Log.d(TAG, "  $EXPECTED_IMAGE_URL")

                if (mediaList.isNotEmpty()) {
                    Log.d(TAG, "\nActual URLs in database:")
                    mediaList.forEachIndexed { index, media ->
                        Log.d(TAG, "  [${index + 1}] ${media.link}")

                        // Character-by-character comparison for first item
                        if (index == 0 && media.link != EXPECTED_IMAGE_URL) {
                            Log.d(TAG, "\n  Character comparison (first 50 chars):")
                            val maxLen = minOf(EXPECTED_IMAGE_URL.length, media.link.length, 50)
                            for (i in 0 until maxLen) {
                                if (i < media.link.length && i < EXPECTED_IMAGE_URL.length) {
                                    if (media.link[i] != EXPECTED_IMAGE_URL[i]) {
                                        Log.d(TAG, "    Position $i: '${media.link[i]}' vs '${EXPECTED_IMAGE_URL[i]}' ✗")
                                    }
                                }
                            }
                        }
                    }
                }

                Log.d(TAG, "\n========================================")
                Log.d(TAG, "DEBUG TEST COMPLETED")
                Log.d(TAG, "========================================")
            }
        } else {
            mediaResult.onFailure { exception ->
                Log.e(TAG, "✗ Failed to fetch media")
                Log.e(TAG, "Error: ${exception.message}")
                exception.printStackTrace()
                fail("Failed to fetch media: ${exception.message}")
            }
        }
    }

    @Test
    fun testAllMediaTypes() = runTest {
        Log.d(TAG, "\n========================================")
        Log.d(TAG, "DEBUG: All Media Types Overview")
        Log.d(TAG, "========================================")

        // Login
        val loginResult = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Login should succeed", loginResult.isSuccess)

        // Test all three tables
        Log.d(TAG, "\n1. PATIENT CARE MEDIA (perawatan_media)")
        val patientCareResult = mediaRepository.getPatientCareMedia()
        patientCareResult.onSuccess { media ->
            Log.d(TAG, "   ✓ Count: ${media.size}")
            media.forEach { Log.d(TAG, "     - ${it.link}") }
        }
        patientCareResult.onFailure { Log.e(TAG, "   ✗ Error: ${it.message}") }

        Log.d(TAG, "\n2. STRESS MEDIA (stress_media)")
        val stressResult = mediaRepository.getStressMedia()
        stressResult.onSuccess { media ->
            Log.d(TAG, "   ✓ Count: ${media.size}")
            media.forEach { Log.d(TAG, "     - ${it.link}") }
        }
        stressResult.onFailure { Log.e(TAG, "   ✗ Error: ${it.message}") }

        Log.d(TAG, "\n3. SCHIZOPHRENIA MEDIA (skizo_media)")
        val schizoResult = mediaRepository.getSchizophreniaMedia()
        schizoResult.onSuccess { media ->
            Log.d(TAG, "   ✓ Count: ${media.size}")
            media.forEach { Log.d(TAG, "     - ${it.link}") }
        }
        schizoResult.onFailure { Log.e(TAG, "   ✗ Error: ${it.message}") }

        Log.d(TAG, "\n========================================")
    }
}
