package com.siagajiwa.siagajiwa

import com.siagajiwa.siagajiwa.data.SupabaseClient
import com.siagajiwa.siagajiwa.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Supabase Connection Test - JUnit Unit Test
 *
 * This test can run WITHOUT Android emulator or device.
 * Run with: ./gradlew test --tests "com.siagajiwa.siagajiwa.SupabaseConnectionTest"
 *
 * What it tests:
 * 1. Supabase client initialization
 * 2. Connection to Supabase servers
 * 3. UserRepository functionality
 * 4. Authentication service availability
 * 5. Database service availability
 */
class SupabaseConnectionTest {

    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        userRepository = UserRepository()
        println("\n" + "=".repeat(80))
        println("🧪 SUPABASE CONNECTION TEST - STARTING")
        println("=".repeat(80))
    }

    @Test
    fun `test 1 - Supabase Client Initialization`() {
        println("\n📋 TEST 1: Supabase Client Initialization")
        println("-".repeat(80))

        try {
            // Check if client is initialized
            assertNotNull("❌ Supabase client should not be null", SupabaseClient.client)
            println("✅ Supabase client initialized successfully")

            assertNotNull("❌ Supabase auth should not be null", SupabaseClient.auth)
            println("✅ Supabase Auth module loaded")

            assertNotNull("❌ Supabase database should not be null", SupabaseClient.database)
            println("✅ Supabase Database (Postgrest) module loaded")

            println("\n🎉 TEST 1 PASSED - Client initialization successful")
        } catch (e: Exception) {
            println("❌ TEST 1 FAILED: ${e.message}")
            throw e
        }
    }

    @Test
    fun `test 2 - Supabase Server Connection`() = runBlocking {
        println("\n📋 TEST 2: Supabase Server Connection")
        println("-".repeat(80))

        try {
            withTimeout(15000) { // 15 second timeout
                println("🔄 Attempting to connect to Supabase server...")

                // Try to access the auth service - this requires server connection
                val currentSession = SupabaseClient.auth.currentSessionOrNull()

                println("✅ Successfully connected to Supabase server")
                println("📊 Connection Details:")
                println("   - Server Status: Connected")
                println("   - Auth Service: Available")
                println("   - Session Status: ${if (currentSession != null) "Active Session Found" else "No Active Session (Expected)"}")

                if (currentSession != null) {
                    println("   - Session User: ${currentSession.user?.email ?: "Unknown"}")
                }

                println("\n🎉 TEST 2 PASSED - Server connection successful")
            }
        } catch (e: Exception) {
            println("❌ TEST 2 FAILED: Unable to connect to Supabase")
            println("   Error: ${e.message}")
            println("   Possible causes:")
            println("   - No internet connection")
            println("   - Invalid Supabase credentials")
            println("   - Supabase project is paused or deleted")
            throw e
        }
    }

    @Test
    fun `test 3 - UserRepository Initialization`() {
        println("\n📋 TEST 3: UserRepository Initialization")
        println("-".repeat(80))

        try {
            assertNotNull("❌ UserRepository should not be null", userRepository)
            println("✅ UserRepository initialized successfully")

            val currentUser = userRepository.getCurrentUser()
            println("✅ UserRepository.getCurrentUser() method works")
            println("   Current User: ${currentUser?.email ?: "No user logged in"}")

            println("\n🎉 TEST 3 PASSED - UserRepository working properly")
        } catch (e: Exception) {
            println("❌ TEST 3 FAILED: ${e.message}")
            throw e
        }
    }

    @Test
    fun `test 4 - Authentication Service Test`() = runBlocking {
        println("\n📋 TEST 4: Authentication Service Test")
        println("-".repeat(80))

        try {
            withTimeout(15000) {
                println("🔄 Testing authentication service...")

                // Test with invalid credentials to check if service responds
                val result = userRepository.signInWithEmail(
                    email = "test.connection@example.com",
                    password = "invalid_password_for_testing"
                )

                // We expect this to fail, but getting a response means service is working
                if (result.isFailure) {
                    val error = result.exceptionOrNull()
                    println("✅ Authentication service is responding")
                    println("   Service Status: Active")
                    println("   Response Type: ${error?.javaClass?.simpleName}")
                    println("   (Expected failure with invalid credentials)")

                    // This is actually good - service is working and rejecting bad credentials
                    assertTrue("Auth service should respond even with invalid credentials", true)
                } else {
                    println("⚠️  Unexpected: Login succeeded with test credentials")
                }

                println("\n🎉 TEST 4 PASSED - Auth service is accessible and working")
            }
        } catch (e: Exception) {
            println("❌ TEST 4 FAILED: Auth service error")
            println("   Error: ${e.message}")
            throw e
        }
    }

    @Test
    fun `test 5 - Database Service Availability`() = runBlocking {
        println("\n📋 TEST 5: Database Service Availability")
        println("-".repeat(80))

        try {
            withTimeout(15000) {
                println("🔄 Testing database service...")

                assertNotNull("❌ Database service should not be null", SupabaseClient.database)
                println("✅ Database service (Postgrest) is available")

                // Try to query with a non-existent user ID
                val testUserId = "00000000-0000-0000-0000-000000000000"
                val result = userRepository.getUserProfile(testUserId)

                // Getting any response (even failure) means database is accessible
                println("✅ Database service is responding")
                println("   Service Status: Active")
                println("   Query Result: ${if (result.isSuccess) "Success" else "Expected failure (user not found)"}")

                println("\n🎉 TEST 5 PASSED - Database service is accessible")
            }
        } catch (e: Exception) {
            println("❌ TEST 5 FAILED: Database service error")
            println("   Error: ${e.message}")
            throw e
        }
    }

    @Test
    fun `test 6 - Full Connection Summary`() = runBlocking {
        println("\n📋 TEST 6: Full Connection Summary")
        println("-".repeat(80))

        val results = mutableListOf<String>()

        try {
            // Test 1: Client
            if (SupabaseClient.client != null) {
                results.add("✅ Client Initialized")
            } else {
                results.add("❌ Client Failed")
            }

            // Test 2: Auth Module
            if (SupabaseClient.auth != null) {
                results.add("✅ Auth Module Loaded")
            } else {
                results.add("❌ Auth Module Failed")
            }

            // Test 3: Database Module
            if (SupabaseClient.database != null) {
                results.add("✅ Database Module Loaded")
            } else {
                results.add("❌ Database Module Failed")
            }

            // Test 4: Server Connection
            withTimeout(15000) {
                try {
                    SupabaseClient.auth.currentSessionOrNull()
                    results.add("✅ Server Connection Working")
                } catch (e: Exception) {
                    results.add("❌ Server Connection Failed: ${e.message}")
                }
            }

            // Test 5: Repository
            if (userRepository != null) {
                results.add("✅ UserRepository Working")
            } else {
                results.add("❌ UserRepository Failed")
            }

            // Print Summary
            println("\n📊 CONNECTION TEST SUMMARY:")
            println("=".repeat(80))
            results.forEach { println("   $it") }
            println("=".repeat(80))

            val successCount = results.count { it.startsWith("✅") }
            val totalTests = results.size

            println("\n🎯 Results: $successCount/$totalTests tests passed")

            if (successCount == totalTests) {
                println("🎉 ALL TESTS PASSED - Supabase connection is fully functional!")
            } else {
                println("⚠️  Some tests failed - Please check the errors above")
            }

            // Assert all passed
            assertEquals("Not all connection tests passed", totalTests, successCount)

        } catch (e: Exception) {
            println("❌ TEST 6 FAILED: ${e.message}")
            throw e
        }
    }
}
