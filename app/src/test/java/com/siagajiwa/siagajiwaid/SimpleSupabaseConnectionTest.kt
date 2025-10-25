package com.siagajiwa.siagajiwaid

import org.junit.Test
import org.junit.Assert.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * Simple Supabase Connection Test - Pure JUnit (No Android Dependencies)
 *
 * Run with: ./gradlew :app:testDebugUnitTest --tests "com.siagajiwa.siagajiwaid.SimpleSupabaseConnectionTest"
 *
 * This test verifies:
 * 1. Supabase server is reachable
 * 2. API endpoints are accessible
 * 3. Authentication is configured correctly
 *
 * No Android device/emulator needed!
 */
class SimpleSupabaseConnectionTest {

    // Your Supabase configuration
    private val SUPABASE_URL = "https://uharlzitusomrqtuitqz.supabase.co"
    private val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVoYXJseml0dXNvbXJxdHVpdHF6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTM2OTQyNTUsImV4cCI6MjA2OTI3MDI1NX0.7ffrDwZMx1Qores-tWoy8617h6O41vLmvYeJXAhJbjw"

    @Test
    fun `test 1 - Supabase Server Reachability`() {
        println("\n" + "=".repeat(80))
        println("🧪 SUPABASE CONNECTION TEST - STARTING")
        println("=".repeat(80))
        println("\n📋 TEST 1: Supabase Server Reachability")
        println("-".repeat(80))

        try {
            val url = URL(SUPABASE_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)

            val responseCode = connection.responseCode

            println("🔄 Connecting to: $SUPABASE_URL")
            println("📊 Response Code: $responseCode")

            when (responseCode) {
                in 200..299 -> {
                    println("✅ Server is online and responding")
                    println("✅ HTTP Status: ${connection.responseMessage}")
                }
                in 300..399 -> {
                    println("⚠️  Server returned redirect (${connection.responseMessage})")
                }
                in 400..499 -> {
                    println("✅ Server is reachable (returned client error - expected for base URL)")
                    println("   Status: ${connection.responseMessage}")
                }
                in 500..599 -> {
                    println("❌ Server error: ${connection.responseMessage}")
                    fail("Server returned error: $responseCode")
                }
            }

            connection.disconnect()

            assertTrue("Server should be reachable", responseCode in 200..499)
            println("\n🎉 TEST 1 PASSED - Server is reachable")

        } catch (e: Exception) {
            println("❌ TEST 1 FAILED: ${e.message}")
            println("   Possible causes:")
            println("   - No internet connection")
            println("   - Supabase server is down")
            println("   - Firewall blocking connection")
            throw e
        }
    }

    @Test
    fun `test 2 - Supabase REST API Endpoint`() {
        println("\n📋 TEST 2: Supabase REST API Endpoint")
        println("-".repeat(80))

        try {
            // Test the REST API endpoint
            val apiUrl = "$SUPABASE_URL/rest/v1/"
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)
            connection.setRequestProperty("Authorization", "Bearer $SUPABASE_ANON_KEY")

            val responseCode = connection.responseCode

            println("🔄 Testing REST API: $apiUrl")
            println("📊 Response Code: $responseCode")

            when (responseCode) {
                200, 404 -> {
                    println("✅ REST API is accessible")
                    println("✅ Authentication key is valid")
                }
                401, 403 -> {
                    println("❌ Authentication failed")
                    fail("Invalid API key or authentication error")
                }
                else -> {
                    println("⚠️  Unexpected response: ${connection.responseMessage}")
                }
            }

            connection.disconnect()

            assertTrue("REST API should be accessible", responseCode in listOf(200, 404))
            println("\n🎉 TEST 2 PASSED - REST API is working")

        } catch (e: Exception) {
            println("❌ TEST 2 FAILED: ${e.message}")
            throw e
        }
    }

    @Test
    fun `test 3 - Supabase Auth Endpoint`() {
        println("\n📋 TEST 3: Supabase Auth Endpoint")
        println("-".repeat(80))

        try {
            // Test the Auth API endpoint
            val authUrl = "$SUPABASE_URL/auth/v1/health"
            val url = URL(authUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)

            val responseCode = connection.responseCode

            println("🔄 Testing Auth API: $authUrl")
            println("📊 Response Code: $responseCode")

            if (responseCode == 200) {
                // Read response
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("✅ Auth service is healthy")
                println("📦 Response: $response")
            } else {
                println("⚠️  Auth endpoint returned: ${connection.responseMessage}")
            }

            connection.disconnect()

            assertTrue("Auth API should be accessible", responseCode in 200..404)
            println("\n🎉 TEST 3 PASSED - Auth service is accessible")

        } catch (e: Exception) {
            println("❌ TEST 3 FAILED: ${e.message}")
            throw e
        }
    }

    @Test
    fun `test 4 - Supabase Configuration Verification`() {
        println("\n📋 TEST 4: Configuration Verification")
        println("-".repeat(80))

        try {
            println("🔍 Verifying Supabase Configuration:")
            println("   URL: $SUPABASE_URL")
            println("   Project: uharlzitusomrqtuitqz")
            println("   API Key Length: ${SUPABASE_ANON_KEY.length} characters")

            // Validate URL format
            val urlPattern = "https://[a-z]+\\.supabase\\.co".toRegex()
            assertTrue("URL format should be valid", SUPABASE_URL.matches(urlPattern))
            println("✅ URL format is valid")

            // Validate API key format (JWT structure)
            val keyParts = SUPABASE_ANON_KEY.split(".")
            assertTrue("API key should be a valid JWT (3 parts)", keyParts.size == 3)
            println("✅ API key format is valid (JWT structure)")

            // Extract project reference from URL
            val projectRef = SUPABASE_URL.substringAfter("https://").substringBefore(".supabase.co")
            assertEquals("Project reference should match", "uharlzitusomrqtuitqz", projectRef)
            println("✅ Project reference matches: $projectRef")

            println("\n🎉 TEST 4 PASSED - Configuration is valid")

        } catch (e: Exception) {
            println("❌ TEST 4 FAILED: ${e.message}")
            throw e
        }
    }

    @Test
    fun `test 5 - Full Connection Summary`() {
        println("\n📋 TEST 5: Full Connection Summary")
        println("-".repeat(80))

        val results = mutableMapOf<String, Boolean>()

        // Test 1: Server reachability
        try {
            val url = URL(SUPABASE_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)
            val responseCode = connection.responseCode
            connection.disconnect()
            results["Server Reachable"] = responseCode in 200..499
        } catch (e: Exception) {
            results["Server Reachable"] = false
        }

        // Test 2: REST API
        try {
            val url = URL("$SUPABASE_URL/rest/v1/")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)
            connection.setRequestProperty("Authorization", "Bearer $SUPABASE_ANON_KEY")
            val responseCode = connection.responseCode
            connection.disconnect()
            results["REST API"] = responseCode in listOf(200, 404)
        } catch (e: Exception) {
            results["REST API"] = false
        }

        // Test 3: Auth API
        try {
            val url = URL("$SUPABASE_URL/auth/v1/health")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)
            val responseCode = connection.responseCode
            connection.disconnect()
            results["Auth API"] = responseCode in 200..404
        } catch (e: Exception) {
            results["Auth API"] = false
        }

        // Test 4: Configuration
        results["Configuration Valid"] = try {
            SUPABASE_URL.matches("https://[a-z]+\\.supabase\\.co".toRegex()) &&
            SUPABASE_ANON_KEY.split(".").size == 3
        } catch (e: Exception) {
            false
        }

        // Print Summary
        println("\n📊 SUPABASE CONNECTION TEST SUMMARY:")
        println("=".repeat(80))
        results.forEach { (test, passed) ->
            val icon = if (passed) "✅" else "❌"
            val status = if (passed) "PASSED" else "FAILED"
            println("   $icon $test: $status")
        }
        println("=".repeat(80))

        val passedCount = results.values.count { it }
        val totalTests = results.size

        println("\n🎯 Results: $passedCount/$totalTests tests passed (${passedCount * 100 / totalTests}%)")

        if (passedCount == totalTests) {
            println("🎉 ALL TESTS PASSED - Supabase is fully functional!")
            println("\n✨ Your Supabase connection is working perfectly!")
            println("   You can now use UserRepository in your Android app.")
        } else {
            val failedTests = results.filter { !it.value }.keys
            println("⚠️  Some tests failed:")
            failedTests.forEach { println("   - $it") }
            println("\n💡 Troubleshooting:")
            println("   1. Check your internet connection")
            println("   2. Verify Supabase project is active at https://supabase.com/dashboard")
            println("   3. Confirm API keys are correct in SupabaseClient.kt")
        }

        println("\n" + "=".repeat(80))
        println("🏁 TEST SUITE COMPLETED")
        println("=".repeat(80) + "\n")

        // Assert all passed
        assertEquals("Not all connection tests passed", totalTests, passedCount)
    }
}
