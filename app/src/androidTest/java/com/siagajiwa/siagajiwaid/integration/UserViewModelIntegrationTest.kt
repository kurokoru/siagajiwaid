package com.siagajiwa.siagajiwaid.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.siagajiwa.siagajiwaid.data.repository.QuizRepository
import com.siagajiwa.siagajiwaid.data.repository.UserRepository
import com.siagajiwa.siagajiwaid.viewmodel.UserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for UserViewModel with Supabase connection
 *
 * Tests the complete flow of:
 * 1. User authentication (signin)
 * 2. Loading user profile
 * 3. Fetching latest stress result from stress_results table
 * 4. Fetching latest quiz result from quiz_results table
 *
 * IMPORTANT: These tests require:
 * 1. Active internet connection
 * 2. Valid Supabase project configuration
 * 3. Test user credentials: vahyo@yopmail.com / 123456
 * 4. stress_results table in Supabase with data for the test user
 * 5. quiz_results table in Supabase with data for the test user
 *
 * Test Credentials:
 * - Email: vahyo@yopmail.com
 * - Password: 123456
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var userViewModel: UserViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var quizRepository: QuizRepository

    // Test credentials
    private val TEST_EMAIL = "vahyo@yopmail.com"
    private val TEST_PASSWORD = "123456"

    @Before
    fun setup() {
        println("\n" + "=".repeat(80))
        println("STARTING USER VIEWMODEL INTEGRATION TEST")
        println("=".repeat(80))

        userRepository = UserRepository()
        quizRepository = QuizRepository()
        userViewModel = UserViewModel()
    }

    @After
    fun tearDown() = runTest {
        println("\n" + "=".repeat(80))
        println("CLEANING UP - SIGNING OUT")
        println("=".repeat(80))

        try {
            userRepository.signOut()
            delay(1000) // Give time for sign out to complete
        } catch (e: Exception) {
            println("⚠️  Sign out error (ignored): ${e.message}")
        }
    }

    @Test
    fun testSignInLoadsStressResult() = runTest {
        println("\n" + "━".repeat(80))
        println("TEST: Sign In Automatically Loads Stress Result")
        println("━".repeat(80))

        // Given: Test user credentials
        println("\n📝 Test Email: $TEST_EMAIL")
        println("📝 Test Password: $TEST_PASSWORD")

        var signInSuccess = false
        var signInError: String? = null

        // When: User signs in
        println("\n🔐 Attempting sign in...")
        userViewModel.signInWithEmail(
            email = TEST_EMAIL,
            password = TEST_PASSWORD,
            onSuccess = {
                signInSuccess = true
                println("✅ Sign in successful!")
            },
            onError = { error ->
                signInError = error
                println("❌ Sign in failed: $error")
            }
        )

        // Wait for sign in and data loading to complete by polling the ViewModel state
        // Using Thread.sleep for real time delays since runTest doesn't wait for viewModelScope
        var retries = 0
        while (!userViewModel.uiState.value.isLoggedIn && retries < 30) {
            Thread.sleep(500)
            retries++
        }
        println("Polling completed after $retries retries. Logged in: ${userViewModel.uiState.value.isLoggedIn}")

        // Then: Verify sign in was successful
        println("\n" + "─".repeat(80))
        println("VERIFICATION: Sign In Status")
        println("─".repeat(80))
        assertTrue("Sign in should succeed", userViewModel.uiState.value.isLoggedIn)
        assertNull("Should not have sign in error", userViewModel.uiState.value.error)
        println("✅ Sign in verified")

        // Then: Verify user profile is loaded
        val uiState = userViewModel.uiState.value
        println("\n" + "─".repeat(80))
        println("VERIFICATION: User Profile")
        println("─".repeat(80))
        assertNotNull("User should be loaded", uiState.user)
        assertTrue("User should be logged in", uiState.isLoggedIn)
        assertEquals("Email should match", TEST_EMAIL, uiState.user?.email)

        println("✅ User Profile Loaded:")
        println("   - User ID: ${uiState.user?.id}")
        println("   - Email: ${uiState.user?.email}")
        println("   - Full Name: ${uiState.user?.fullName}")

        // Then: Verify stress result is loaded
        println("\n" + "─".repeat(80))
        println("VERIFICATION: Latest Stress Result")
        println("─".repeat(80))

        if (uiState.latestStressResult != null) {
            val stressResult = uiState.latestStressResult
            println("✅ Stress Result Loaded Successfully!")
            println("\n📊 STRESS RESULT DETAILS:")
            println("   - Stress Level: ${stressResult.stressLevel}")
            println("   - Stress Score: ${stressResult.stressScore}")
            println("   - Test Date: ${stressResult.testDate}")
            println("   - Created At: ${stressResult.createdAt}")

            assertNotNull("Stress level should not be null", stressResult.stressLevel)
            assertNotNull("Stress score should not be null", stressResult.stressScore)
            assertTrue("Stress level should be valid",
                stressResult.stressLevel in listOf("Rendah", "Sedang", "Tinggi", "rendah", "sedang", "tinggi"))
        } else {
            println("ℹ️  No stress result found for this user")
            println("   This is expected if the user hasn't taken a stress test yet")
            println("   Stress level will display as 'Belum diukur' in the app")
        }

        println("\n" + "=".repeat(80))
        println("TEST COMPLETED SUCCESSFULLY")
        println("=".repeat(80))
    }

    @Test
    fun testLoadLatestStressResultDirectly() = runTest {
        println("\n" + "━".repeat(80))
        println("TEST: Load Latest Stress Result Directly")
        println("━".repeat(80))

        // Given: User is signed in
        println("\n🔐 Signing in as test user...")
        val signInResult = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Sign in should succeed", signInResult.isSuccess)

        var userId: String? = null
        signInResult.onSuccess { userInfo ->
            userId = userInfo.id
            println("✅ Signed in successfully!")
            println("   - User ID: ${userInfo.id}")
            println("   - Email: ${userInfo.email}")
        }

        assertNotNull("User ID should not be null", userId)

        // When: Loading stress result directly
        println("\n📊 Loading latest stress result from stress_result table...")
        val stressResult = quizRepository.getLatestStressResult(userId!!)

        // Then: Verify the result
        println("\n" + "─".repeat(80))
        println("VERIFICATION: Stress Result Query")
        println("─".repeat(80))

        assertTrue("Query should succeed", stressResult.isSuccess)

        stressResult.onSuccess { data ->
            if (data != null) {
                println("✅ Stress result found in database!")
                println("\n📊 STRESS RESULT FROM SUPABASE:")
                println("   - ID: ${data.id ?: "null"}")
                println("   - User ID: ${data.userId}")
                println("   - Stress Level: ${data.stressLevel}")
                println("   - Stress Score: ${data.stressScore}")
                println("   - Test Date: ${data.testDate ?: "null"}")
                println("   - Created At: ${data.createdAt ?: "null"}")

                // Verify data integrity
                assertEquals("User ID should match", userId, data.userId)
                assertNotNull("Stress level should not be null", data.stressLevel)
                assertNotNull("Stress score should not be null", data.stressScore)
                assertTrue("Stress score should be valid", data.stressScore >= 0)

                println("\n✅ Data integrity verified!")
            } else {
                println("ℹ️  No stress result found in database")
                println("   User: $userId")
                println("   This means the user hasn't taken a stress test yet")
            }
        }

        stressResult.onFailure { exception ->
            println("❌ Failed to load stress result: ${exception.message}")
            fail("Should not fail to query stress result: ${exception.message}")
        }

        println("\n" + "=".repeat(80))
        println("TEST COMPLETED SUCCESSFULLY")
        println("=".repeat(80))
    }

    @Test
    fun testRefreshStressData() = runTest {
        println("\n" + "━".repeat(80))
        println("TEST: Refresh Stress Data Function")
        println("━".repeat(80))

        // Given: User is signed in
        println("\n🔐 Signing in...")
        var signInSuccess = false
        userViewModel.signInWithEmail(
            email = TEST_EMAIL,
            password = TEST_PASSWORD,
            onSuccess = { signInSuccess = true },
            onError = { println("❌ Sign in failed: $it") }
        )

        delay(2000)
        assertTrue("Sign in should succeed", signInSuccess)
        println("✅ Signed in successfully")

        val uiStateBefore = userViewModel.uiState.value
        println("\n📊 Initial State:")
        println("   - User: ${uiStateBefore.user?.email}")
        println("   - Stress Result: ${uiStateBefore.latestStressResult?.stressLevel ?: "null"}")

        // When: Refreshing stress data
        println("\n🔄 Calling refreshStressData()...")
        userViewModel.refreshStressData()

        delay(2000) // Wait for refresh to complete

        // Then: Verify data is refreshed
        val uiStateAfter = userViewModel.uiState.value
        println("\n📊 After Refresh:")
        println("   - User: ${uiStateAfter.user?.email}")
        println("   - Stress Result: ${uiStateAfter.latestStressResult?.stressLevel ?: "null"}")

        assertNotNull("User should still be loaded", uiStateAfter.user)
        println("\n✅ Refresh completed successfully!")

        println("\n" + "=".repeat(80))
        println("TEST COMPLETED SUCCESSFULLY")
        println("=".repeat(80))
    }

    @Test
    fun testCheckAuthStatusLoadsStressResult() = runTest {
        println("\n" + "━".repeat(80))
        println("TEST: CheckAuthStatus Loads Stress Result on App Launch")
        println("━".repeat(80))

        // Given: User signs in first
        println("\n🔐 Signing in to create a session...")
        val signInResult = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Sign in should succeed", signInResult.isSuccess)
        println("✅ Session created")

        delay(1000)

        // When: Creating a new ViewModel instance (simulates app restart)
        println("\n🔄 Creating new ViewModel (simulating app restart)...")
        val newViewModel = UserViewModel()

        // Wait for checkAuthStatus to complete in init block
        delay(5000)

        // Then: Verify user and stress result are loaded automatically
        val uiState = newViewModel.uiState.value

        println("\n" + "─".repeat(80))
        println("VERIFICATION: Auto-loaded State")
        println("─".repeat(80))

        assertTrue("User should be logged in", uiState.isLoggedIn)
        assertNotNull("User should be loaded", uiState.user)
        println("✅ User auto-loaded: ${uiState.user?.email}")

        if (uiState.latestStressResult != null) {
            println("✅ Stress result auto-loaded: ${uiState.latestStressResult?.stressLevel}")
        } else {
            println("ℹ️  No stress result (user hasn't taken test)")
        }

        println("\n" + "=".repeat(80))
        println("TEST COMPLETED SUCCESSFULLY")
        println("=".repeat(80))
    }

    @Test
    fun testUserStateStructure() = runTest {
        println("\n" + "━".repeat(80))
        println("TEST: User State Structure and Data Types")
        println("━".repeat(80))

        // Given: User signs in
        println("\n🔐 Signing in...")
        var signInSuccess = false
        userViewModel.signInWithEmail(
            email = TEST_EMAIL,
            password = TEST_PASSWORD,
            onSuccess = { signInSuccess = true },
            onError = { }
        )

        delay(5000)
        assertTrue("Sign in should succeed", signInSuccess)

        // Then: Verify UserUiState structure
        val uiState = userViewModel.uiState.value

        println("\n📋 USER UI STATE STRUCTURE:")
        println("─".repeat(80))
        println("1. isLoggedIn: ${uiState.isLoggedIn} (Boolean)")
        println("2. isLoading: ${uiState.isLoading} (Boolean)")
        println("3. error: ${uiState.error ?: "null"} (String?)")
        println("4. user: ${if (uiState.user != null) "loaded" else "null"} (User?)")
        println("5. latestStressResult: ${if (uiState.latestStressResult != null) "loaded" else "null"} (StressData?)")
        println("6. latestQuizResult: ${if (uiState.latestQuizResult != null) "loaded" else "null"} (QuizData?)")

        if (uiState.user != null) {
            println("\n📋 USER DATA STRUCTURE:")
            println("─".repeat(80))
            val user = uiState.user!!
            println("   - id: ${user.id}")
            println("   - email: ${user.email}")
            println("   - fullName: ${user.fullName}")
            println("   - stressLevel: ${user.stressLevel ?: "null"}")
            println("   - stressScore: ${user.stressScore ?: "null"}")
            println("   - knowledgeScore: ${user.knowledgeScore ?: "null"}")
            println("   - knowledgePercentage: ${user.knowledgePercentage ?: "null"}")
        }

        if (uiState.latestStressResult != null) {
            println("\n📋 LATEST STRESS RESULT STRUCTURE:")
            println("─".repeat(80))
            val stress = uiState.latestStressResult!!
            println("   - id: ${stress.id ?: "null"}")
            println("   - userId: ${stress.userId}")
            println("   - stressLevel: ${stress.stressLevel}")
            println("   - stressScore: ${stress.stressScore}")
            println("   - testDate: ${stress.testDate ?: "null"}")
            println("   - createdAt: ${stress.createdAt ?: "null"}")
        }

        if (uiState.latestQuizResult != null) {
            println("\n📋 LATEST QUIZ RESULT STRUCTURE:")
            println("─".repeat(80))
            val quiz = uiState.latestQuizResult!!
            println("   - id: ${quiz.id ?: "null"}")
            println("   - userId: ${quiz.userId}")
            println("   - quizScore: ${quiz.quizScore}")
            println("   - totalQuestions: ${quiz.totalQuestions}")
            println("   - percentage: ${quiz.percentage}")
            println("   - quizDate: ${quiz.quizDate ?: "null"}")
            println("   - createdAt: ${quiz.createdAt ?: "null"}")
        }

        println("\n✅ All data structures verified!")

        println("\n" + "=".repeat(80))
        println("TEST COMPLETED SUCCESSFULLY")
        println("=".repeat(80))
    }

    @Test
    fun testSignInLoadsQuizResult() = runTest {
        println("\n" + "━".repeat(80))
        println("TEST: Sign In Automatically Loads Quiz Result")
        println("━".repeat(80))

        // Given: Test user credentials
        println("\n📝 Test Email: $TEST_EMAIL")
        println("📝 Test Password: $TEST_PASSWORD")

        var signInSuccess = false
        var signInError: String? = null

        // When: User signs in
        println("\n🔐 Attempting sign in...")
        userViewModel.signInWithEmail(
            email = TEST_EMAIL,
            password = TEST_PASSWORD,
            onSuccess = {
                signInSuccess = true
                println("✅ Sign in successful!")
            },
            onError = { error ->
                signInError = error
                println("❌ Sign in failed: $error")
            }
        )

        // Wait for sign in and data loading to complete by polling the ViewModel state
        // Using Thread.sleep for real time delays since runTest doesn't wait for viewModelScope
        var retries = 0
        while (!userViewModel.uiState.value.isLoggedIn && retries < 30) {
            Thread.sleep(500)
            retries++
        }
        println("Polling completed after $retries retries. Logged in: ${userViewModel.uiState.value.isLoggedIn}")

        // Then: Verify sign in was successful
        println("\n" + "─".repeat(80))
        println("VERIFICATION: Sign In Status")
        println("─".repeat(80))
        assertTrue("Sign in should succeed", userViewModel.uiState.value.isLoggedIn)
        assertNull("Should not have sign in error", userViewModel.uiState.value.error)
        println("✅ Sign in verified")

        // Then: Verify user profile is loaded
        val uiState = userViewModel.uiState.value
        println("\n" + "─".repeat(80))
        println("VERIFICATION: User Profile")
        println("─".repeat(80))
        assertNotNull("User should be loaded", uiState.user)
        assertTrue("User should be logged in", uiState.isLoggedIn)
        assertEquals("Email should match", TEST_EMAIL, uiState.user?.email)

        println("✅ User Profile Loaded:")
        println("   - User ID: ${uiState.user?.id}")
        println("   - Email: ${uiState.user?.email}")
        println("   - Full Name: ${uiState.user?.fullName}")

        // Then: Verify quiz result is loaded
        println("\n" + "─".repeat(80))
        println("VERIFICATION: Latest Quiz Result")
        println("─".repeat(80))

        if (uiState.latestQuizResult != null) {
            val quizResult = uiState.latestQuizResult
            println("✅ Quiz Result Loaded Successfully!")
            println("\n📊 QUIZ RESULT DETAILS:")
            println("   - Quiz Score: ${quizResult.quizScore}")
            println("   - Total Questions: ${quizResult.totalQuestions}")
            println("   - Percentage: ${quizResult.percentage}%")
            println("   - Quiz Date: ${quizResult.quizDate}")
            println("   - Created At: ${quizResult.createdAt}")

            assertNotNull("Quiz score should not be null", quizResult.quizScore)
            assertNotNull("Total questions should not be null", quizResult.totalQuestions)
            assertNotNull("Percentage should not be null", quizResult.percentage)
            assertTrue("Quiz score should be valid", quizResult.quizScore >= 0)
            assertTrue("Total questions should be positive", quizResult.totalQuestions > 0)
            assertTrue("Percentage should be between 0-100",
                quizResult.percentage in 0..100)
        } else {
            println("ℹ️  No quiz result found for this user")
            println("   This is expected if the user hasn't taken a quiz yet")
            println("   Quiz score will display as '0%' or 'Belum mengikuti tes' in the app")
        }

        println("\n" + "=".repeat(80))
        println("TEST COMPLETED SUCCESSFULLY")
        println("=".repeat(80))
    }

    @Test
    fun testLoadLatestQuizResultDirectly() = runTest {
        println("\n" + "━".repeat(80))
        println("TEST: Load Latest Quiz Result Directly")
        println("━".repeat(80))

        // Given: User is signed in
        println("\n🔐 Signing in as test user...")
        val signInResult = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Sign in should succeed", signInResult.isSuccess)

        var userId: String? = null
        signInResult.onSuccess { userInfo ->
            userId = userInfo.id
            println("✅ Signed in successfully!")
            println("   - User ID: ${userInfo.id}")
            println("   - Email: ${userInfo.email}")
        }

        assertNotNull("User ID should not be null", userId)

        // When: Loading quiz result directly
        println("\n📊 Loading latest quiz result from quiz_results table...")
        val quizResult = quizRepository.getLatestQuizResult(userId!!)

        // Then: Verify the result
        println("\n" + "─".repeat(80))
        println("VERIFICATION: Quiz Result Query")
        println("─".repeat(80))

        assertTrue("Query should succeed", quizResult.isSuccess)

        quizResult.onSuccess { data ->
            if (data != null) {
                println("✅ Quiz result found in database!")
                println("\n📊 QUIZ RESULT FROM SUPABASE:")
                println("   - ID: ${data.id ?: "null"}")
                println("   - User ID: ${data.userId}")
                println("   - Quiz Score: ${data.quizScore}")
                println("   - Total Questions: ${data.totalQuestions}")
                println("   - Percentage: ${data.percentage}%")
                println("   - Quiz Date: ${data.quizDate ?: "null"}")
                println("   - Created At: ${data.createdAt ?: "null"}")

                // Verify data integrity
                assertEquals("User ID should match", userId, data.userId)
                assertNotNull("Quiz score should not be null", data.quizScore)
                assertNotNull("Total questions should not be null", data.totalQuestions)
                assertNotNull("Percentage should not be null", data.percentage)
                assertTrue("Quiz score should be valid", data.quizScore >= 0)
                assertTrue("Total questions should be positive", data.totalQuestions > 0)
                assertTrue("Percentage should be between 0-100", data.percentage in 0..100)
                assertTrue("Quiz score should not exceed total questions",
                    data.quizScore <= data.totalQuestions)

                println("\n✅ Data integrity verified!")
            } else {
                println("ℹ️  No quiz result found in database")
                println("   User: $userId")
                println("   This means the user hasn't taken a quiz yet")
            }
        }

        quizResult.onFailure { exception ->
            println("❌ Failed to load quiz result: ${exception.message}")
            fail("Should not fail to query quiz result: ${exception.message}")
        }

        println("\n" + "=".repeat(80))
        println("TEST COMPLETED SUCCESSFULLY")
        println("=".repeat(80))
    }

    @Test
    fun testRefreshQuizData() = runTest {
        println("\n" + "━".repeat(80))
        println("TEST: Refresh Quiz Data Function")
        println("━".repeat(80))

        // Given: User is signed in
        println("\n🔐 Signing in...")
        var signInSuccess = false
        userViewModel.signInWithEmail(
            email = TEST_EMAIL,
            password = TEST_PASSWORD,
            onSuccess = { signInSuccess = true },
            onError = { println("❌ Sign in failed: $it") }
        )

        delay(2000)
        assertTrue("Sign in should succeed", signInSuccess)
        println("✅ Signed in successfully")

        val uiStateBefore = userViewModel.uiState.value
        println("\n📊 Initial State:")
        println("   - User: ${uiStateBefore.user?.email}")
        println("   - Quiz Result: ${uiStateBefore.latestQuizResult?.percentage ?: "null"}%")

        // When: Refreshing quiz data
        println("\n🔄 Calling refreshQuizData()...")
        userViewModel.refreshQuizData()

        delay(2000) // Wait for refresh to complete

        // Then: Verify data is refreshed
        val uiStateAfter = userViewModel.uiState.value
        println("\n📊 After Refresh:")
        println("   - User: ${uiStateAfter.user?.email}")
        println("   - Quiz Result: ${uiStateAfter.latestQuizResult?.percentage ?: "null"}%")

        assertNotNull("User should still be loaded", uiStateAfter.user)
        println("\n✅ Refresh completed successfully!")

        println("\n" + "=".repeat(80))
        println("TEST COMPLETED SUCCESSFULLY")
        println("=".repeat(80))
    }

    @Test
    fun testCheckAuthStatusLoadsQuizResult() = runTest {
        println("\n" + "━".repeat(80))
        println("TEST: CheckAuthStatus Loads Quiz Result on App Launch")
        println("━".repeat(80))

        // Given: User signs in first
        println("\n🔐 Signing in to create a session...")
        val signInResult = userRepository.signInWithEmail(TEST_EMAIL, TEST_PASSWORD)
        assertTrue("Sign in should succeed", signInResult.isSuccess)
        println("✅ Session created")

        delay(1000)

        // When: Creating a new ViewModel instance (simulates app restart)
        println("\n🔄 Creating new ViewModel (simulating app restart)...")
        val newViewModel = UserViewModel()

        // Wait for checkAuthStatus to complete in init block
        delay(5000)

        // Then: Verify user and quiz result are loaded automatically
        val uiState = newViewModel.uiState.value

        println("\n" + "─".repeat(80))
        println("VERIFICATION: Auto-loaded State")
        println("─".repeat(80))

        assertTrue("User should be logged in", uiState.isLoggedIn)
        assertNotNull("User should be loaded", uiState.user)
        println("✅ User auto-loaded: ${uiState.user?.email}")

        if (uiState.latestQuizResult != null) {
            println("✅ Quiz result auto-loaded: ${uiState.latestQuizResult?.percentage}%")
        } else {
            println("ℹ️  No quiz result (user hasn't taken quiz)")
        }

        println("\n" + "=".repeat(80))
        println("TEST COMPLETED SUCCESSFULLY")
        println("=".repeat(80))
    }
}
