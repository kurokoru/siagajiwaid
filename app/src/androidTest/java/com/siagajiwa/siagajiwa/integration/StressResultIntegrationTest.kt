package com.siagajiwa.siagajiwa.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.siagajiwa.siagajiwa.data.SupabaseClient
import com.siagajiwa.siagajiwa.data.repository.QuizRepository
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for Stress Test Result database operations
 * Tests real Supabase connection with authenticated user
 *
 * Test User Credentials:
 * Email: vahyo@yopmail.com
 * Password: 123456
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class StressResultIntegrationTest {

    private lateinit var repository: QuizRepository
    private var testUserId: String? = null

    companion object {
        private const val TEST_EMAIL = "vahyo@yopmail.com"
        private const val TEST_PASSWORD = "123456"
    }

    @Before
    fun setup() = runTest {
        repository = QuizRepository()

        println("\n========================================")
        println("SETTING UP STRESS RESULT TEST")
        println("========================================\n")

        // Login with test user
        try {
            SupabaseClient.auth.signInWith(Email) {
                email = TEST_EMAIL
                password = TEST_PASSWORD
            }

            testUserId = SupabaseClient.auth.currentUserOrNull()?.id
            println("✅ Successfully logged in as: $TEST_EMAIL")
            println("   User ID: $testUserId\n")
        } catch (e: Exception) {
            println("❌ Failed to login: ${e.message}")
            println("   Make sure the test user exists in Supabase Auth\n")
            throw e
        }
    }

    @After
    fun tearDown() = runTest {
        println("\n========================================")
        println("CLEANING UP - SIGNING OUT")
        println("========================================\n")

        try {
            SupabaseClient.auth.signOut()
            println("✅ Successfully signed out")
        } catch (e: Exception) {
            println("⚠️  Sign out error (ignored): ${e.message}")
        }
    }

    @Test
    fun test_insertStressResult_insert_low_stress_level_result() = runTest {
        println("\n========================================")
        println("TEST: INSERT LOW STRESS RESULT")
        println("========================================\n")

        val userId = testUserId ?: run {
            println("❌ No user ID available")
            return@runTest
        }

        // Test data for LOW stress level (score 0-13)
        val stressLevel = "Rendah"
        val stressScore = 10 // Low stress score

        println("Test Data:")
        println("  User ID: $userId")
        println("  Stress Level: $stressLevel")
        println("  Stress Score: $stressScore")
        println("  Expected Range: 0-13 (Low)\n")

        val result = repository.insertStressResult(
            userId = userId,
            stressLevel = stressLevel,
            stressScore = stressScore
        )

        result.fold(
            onSuccess = { resultId ->
                println("✅ Successfully inserted stress result")
                println("   Result ID: $resultId")
                println("   Stress Level: $stressLevel")
                println("   Stress Score: $stressScore")
                println("========================================\n")

                assertNotNull("Result ID should not be null", resultId)
                assertTrue("Result ID should be a valid UUID", resultId.isNotEmpty())
            },
            onFailure = { error ->
                println("❌ Failed to insert stress result: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
                println("========================================\n")
                fail("Insert should succeed: ${error.message}")
            }
        )
    }

    @Test
    fun test_insertStressResult_insert_medium_stress_level_result() = runTest {
        println("\n========================================")
        println("TEST: INSERT MEDIUM STRESS RESULT")
        println("========================================\n")

        val userId = testUserId ?: run {
            println("❌ No user ID available")
            return@runTest
        }

        // Test data for MEDIUM stress level (score 14-26)
        val stressLevel = "Sedang"
        val stressScore = 20 // Medium stress score

        println("Test Data:")
        println("  User ID: $userId")
        println("  Stress Level: $stressLevel")
        println("  Stress Score: $stressScore")
        println("  Expected Range: 14-26 (Medium)\n")

        val result = repository.insertStressResult(
            userId = userId,
            stressLevel = stressLevel,
            stressScore = stressScore
        )

        result.fold(
            onSuccess = { resultId ->
                println("✅ Successfully inserted stress result")
                println("   Result ID: $resultId")
                println("   Stress Level: $stressLevel")
                println("   Stress Score: $stressScore")
                println("========================================\n")

                assertNotNull("Result ID should not be null", resultId)
                assertTrue("Result ID should be a valid UUID", resultId.isNotEmpty())
            },
            onFailure = { error ->
                println("❌ Failed to insert stress result: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
                println("========================================\n")
                fail("Insert should succeed: ${error.message}")
            }
        )
    }

    @Test
    fun test_insertStressResult_insert_high_stress_level_result() = runTest {
        println("\n========================================")
        println("TEST: INSERT HIGH STRESS RESULT")
        println("========================================\n")

        val userId = testUserId ?: run {
            println("❌ No user ID available")
            return@runTest
        }

        // Test data for HIGH stress level (score 27-40)
        val stressLevel = "Tinggi"
        val stressScore = 35 // High stress score

        println("Test Data:")
        println("  User ID: $userId")
        println("  Stress Level: $stressLevel")
        println("  Stress Score: $stressScore")
        println("  Expected Range: 27-40 (High)\n")

        val result = repository.insertStressResult(
            userId = userId,
            stressLevel = stressLevel,
            stressScore = stressScore
        )

        result.fold(
            onSuccess = { resultId ->
                println("✅ Successfully inserted stress result")
                println("   Result ID: $resultId")
                println("   Stress Level: $stressLevel")
                println("   Stress Score: $stressScore")
                println("========================================\n")

                assertNotNull("Result ID should not be null", resultId)
                assertTrue("Result ID should be a valid UUID", resultId.isNotEmpty())
            },
            onFailure = { error ->
                println("❌ Failed to insert stress result: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
                println("========================================\n")
                fail("Insert should succeed: ${error.message}")
            }
        )
    }

    @Test
    fun test_getLatestStressResult_retrieve_most_recent_result() = runTest {
        println("\n========================================")
        println("TEST: GET LATEST STRESS RESULT")
        println("========================================\n")

        val userId = testUserId ?: run {
            println("❌ No user ID available")
            return@runTest
        }

        println("Fetching latest stress result for user: $userId\n")

        val result = repository.getLatestStressResult(userId)

        result.fold(
            onSuccess = { stressResult ->
                if (stressResult != null) {
                    println("✅ Successfully retrieved latest stress result")
                    println("   Result ID: ${stressResult.id}")
                    println("   User ID: ${stressResult.userId}")
                    println("   Stress Level: ${stressResult.stressLevel}")
                    println("   Stress Score: ${stressResult.stressScore}")
                    println("   Test Date: ${stressResult.testDate}")
                    println("   Created At: ${stressResult.createdAt}")
                    println("========================================\n")

                    assertNotNull("Stress result should not be null", stressResult)
                    assertEquals("User ID should match", userId, stressResult.userId)
                    assertTrue("Stress score should be valid (0-40)",
                        stressResult.stressScore in 0..40)
                    assertTrue("Stress level should be valid",
                        stressResult.stressLevel in listOf("Rendah", "Sedang", "Tinggi"))
                } else {
                    println("⚠️ No stress results found for this user")
                    println("   This is expected if no tests have been completed yet")
                    println("========================================\n")
                }
            },
            onFailure = { error ->
                println("❌ Failed to retrieve stress result: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
                println("========================================\n")
                fail("Get latest should succeed: ${error.message}")
            }
        )
    }

    @Test
    fun test_getAllStressResults_retrieve_all_results_for_user() = runTest {
        println("\n========================================")
        println("TEST: GET ALL STRESS RESULTS")
        println("========================================\n")

        val userId = testUserId ?: run {
            println("❌ No user ID available")
            return@runTest
        }

        println("Fetching all stress results for user: $userId\n")

        val result = repository.getAllStressResults(userId)

        result.fold(
            onSuccess = { stressResults ->
                println("✅ Successfully retrieved ${stressResults.size} stress result(s)\n")

                if (stressResults.isNotEmpty()) {
                    stressResults.forEachIndexed { index, result ->
                        println("--- Result ${index + 1} ---")
                        println("   Result ID: ${result.id}")
                        println("   Stress Level: ${result.stressLevel}")
                        println("   Stress Score: ${result.stressScore}")
                        println("   Test Date: ${result.testDate}")
                        println("   Created At: ${result.createdAt}")
                        println()
                    }

                    // Statistics
                    val avgScore = stressResults.map { it.stressScore }.average()
                    val levelCounts = stressResults.groupingBy { it.stressLevel }.eachCount()

                    println("----------------------------------------")
                    println("Statistics:")
                    println("  Total Tests: ${stressResults.size}")
                    println("  Average Score: %.2f".format(avgScore))
                    println("  Level Distribution:")
                    levelCounts.forEach { (level, count) ->
                        println("    - $level: $count")
                    }
                    println("========================================\n")

                    assertTrue("Should have at least one result", stressResults.isNotEmpty())
                    stressResults.forEach { result ->
                        assertEquals("User ID should match", userId, result.userId)
                        assertTrue("Stress score should be valid (0-40)",
                            result.stressScore in 0..40)
                    }
                } else {
                    println("⚠️ No stress results found for this user")
                    println("   Run insert tests first to create test data")
                    println("========================================\n")
                }
            },
            onFailure = { error ->
                println("❌ Failed to retrieve stress results: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
                println("========================================\n")
                fail("Get all should succeed: ${error.message}")
            }
        )
    }

    @Test
    fun test_complete_flow_insert_then_retrieve() = runTest {
        println("\n========================================")
        println("TEST: COMPLETE FLOW (INSERT + RETRIEVE)")
        println("========================================\n")

        val userId = testUserId ?: run {
            println("❌ No user ID available")
            return@runTest
        }

        // Step 1: Insert a new stress result
        println("Step 1: Inserting new stress result...")
        val stressLevel = "Sedang"
        val stressScore = 18

        val insertResult = repository.insertStressResult(
            userId = userId,
            stressLevel = stressLevel,
            stressScore = stressScore
        )

        var insertedId: String? = null
        insertResult.fold(
            onSuccess = { resultId ->
                insertedId = resultId
                println("✅ Insert successful - ID: $resultId\n")
            },
            onFailure = { error ->
                println("❌ Insert failed: ${error.message}\n")
                fail("Insert should succeed")
            }
        )

        // Step 2: Retrieve the latest result
        println("Step 2: Retrieving latest stress result...")
        val getResult = repository.getLatestStressResult(userId)

        getResult.fold(
            onSuccess = { result ->
                assertNotNull("Result should exist", result)
                result?.let {
                    println("✅ Retrieve successful")
                    println("   Retrieved ID: ${it.id}")
                    println("   Stress Level: ${it.stressLevel}")
                    println("   Stress Score: ${it.stressScore}")
                    println("   Test Date: ${it.testDate}\n")

                    // Verify the retrieved result matches what we inserted
                    assertEquals("Stress level should match", stressLevel, it.stressLevel)
                    assertEquals("Stress score should match", stressScore, it.stressScore)
                    assertEquals("User ID should match", userId, it.userId)

                    println("✅ All data verified correctly!")
                    println("========================================\n")
                }
            },
            onFailure = { error ->
                println("❌ Retrieve failed: ${error.message}\n")
                fail("Retrieve should succeed")
            }
        )
    }
}
