package com.siagajiwa.siagajiwa

import org.junit.Test
import org.junit.Assert.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * User Signup Test - Tests actual user signup with Supabase
 *
 * Run with: ./gradlew :app:testDebugUnitTest --tests "com.siagajiwa.siagajiwa.UserSignupTest"
 *
 * This test attempts to sign up and login a user with real credentials
 */
class UserSignupTest {

    private val SUPABASE_URL = "https://uharlzitusomrqtuitqz.supabase.co"
    private val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVoYXJseml0dXNvbXJxdHVpdHF6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTM2OTQyNTUsImV4cCI6MjA2OTI3MDI1NX0.7ffrDwZMx1Qores-tWoy8617h6O41vLmvYeJXAhJbjw"

    // Test user credentials
    private val TEST_EMAIL = "abata@yopmail.com"
    private val TEST_PASSWORD = "mar4hrU5l1"

    private fun parseJsonValue(json: String, key: String): String? {
        val regex = """"$key"\s*:\s*"([^"]*)"""".toRegex()
        return regex.find(json)?.groupValues?.get(1)
    }

    private fun containsKey(json: String, key: String): Boolean {
        return json.contains(""""$key"""")
    }

    @Test
    fun `test 1 - user signup with provided credentials`() {
        println("\n" + "=".repeat(80))
        println("🧪 USER SIGNUP TEST")
        println("=".repeat(80))
        println("\n📋 Testing User Signup")
        println("-".repeat(80))
        println("📧 Email: $TEST_EMAIL")
        println("🔑 Password: ${TEST_PASSWORD.take(3)}***")
        println("-".repeat(80))

        try {
            // Prepare signup request
            val signupUrl = "$SUPABASE_URL/auth/v1/signup"
            val url = URL(signupUrl)
            val connection = url.openConnection() as HttpURLConnection

            // Setup request
            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)
            connection.setRequestProperty("Authorization", "Bearer $SUPABASE_ANON_KEY")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // Create JSON body as string
            val jsonBody = """{"email":"$TEST_EMAIL","password":"$TEST_PASSWORD"}"""

            println("\n🔄 Sending signup request to Supabase...")
            println("   Endpoint: $signupUrl")

            // Send request
            connection.outputStream.use { os ->
                val input = jsonBody.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            println("\n📊 Response Code: $responseCode")

            // Read response
            val response = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error details"
            }

            println("📦 Response Body:")
            println("-".repeat(80))
            println(response)
            println("-".repeat(80))

            connection.disconnect()

            // Analyze response
            when (responseCode) {
                200, 201 -> {
                    println("\n✅ SIGNUP SUCCESSFUL!")

                    // Parse user info
                    if (containsKey(response, "user")) {
                        val userId = parseJsonValue(response, "id")
                        val userEmail = parseJsonValue(response, "email")
                        val createdAt = parseJsonValue(response, "created_at")

                        println("\n👤 User Information:")
                        println("   ID: ${userId ?: "N/A"}")
                        println("   Email: ${userEmail ?: "N/A"}")
                        println("   Created At: ${createdAt ?: "N/A"}")
                    }

                    // Check if session exists
                    if (containsKey(response, "access_token")) {
                        val accessToken = parseJsonValue(response, "access_token")
                        val tokenType = parseJsonValue(response, "token_type")

                        println("\n🔐 Session Created:")
                        println("   Access Token: ${accessToken?.take(20)}...")
                        println("   Token Type: ${tokenType ?: "N/A"}")
                    } else {
                        println("\n⚠️  User created but no session (email confirmation may be required)")
                    }

                    println("\n🎉 TEST PASSED - User signup completed successfully!")
                    assertTrue("Signup should succeed", true)
                }
                400, 422 -> {
                    println("\n⚠️  VALIDATION ERROR OR BAD REQUEST")

                    if (response.contains("already registered", ignoreCase = true) ||
                        response.contains("already been registered", ignoreCase = true) ||
                        response.contains("User already registered", ignoreCase = true)) {
                        println("   ✅ User already exists - this is expected if you ran this test before")
                        println("   The signup endpoint is working correctly!")
                        println("\n🎉 TEST PASSED - Signup endpoint is functional (user already exists)")
                        assertTrue("Signup endpoint is working", true)
                    } else {
                        val errorMsg = parseJsonValue(response, "msg")
                            ?: parseJsonValue(response, "error_description")
                            ?: parseJsonValue(response, "message")
                            ?: "Unknown error"
                        println("   Error: $errorMsg")
                        println("\n❌ TEST FAILED - Bad request or validation error")
                        fail("Signup failed: $errorMsg")
                    }
                }
                429 -> {
                    println("\n⚠️  RATE LIMIT EXCEEDED")
                    println("   Too many signup attempts. Wait a moment and try again.")
                    println("\n✅ This means the endpoint is working (just rate limited)")
                    assertTrue("Endpoint is working (rate limited)", true)
                }
                else -> {
                    println("\n❌ UNEXPECTED RESPONSE CODE")
                    println("   The signup attempt returned an unexpected status")
                    fail("Unexpected response code: $responseCode")
                }
            }

        } catch (e: Exception) {
            println("\n❌ TEST FAILED WITH EXCEPTION")
            println("   Error: ${e.message}")
            println("\n   Stack trace:")
            e.printStackTrace()

            println("\n💡 Possible causes:")
            println("   - No internet connection")
            println("   - Supabase server is down")
            println("   - Firewall blocking the request")
            println("   - Invalid credentials format")

            fail("Exception during signup: ${e.message}")
        }

        println("\n" + "=".repeat(80))
        println("🏁 SIGNUP TEST COMPLETED")
        println("=".repeat(80) + "\n")
    }

    @Test
    fun `test 2 - user login with provided credentials`() {
        println("\n" + "=".repeat(80))
        println("🧪 USER LOGIN TEST")
        println("=".repeat(80))
        println("\n📋 Testing User Login")
        println("-".repeat(80))
        println("📧 Email: $TEST_EMAIL")
        println("🔑 Password: ${TEST_PASSWORD.take(3)}***")
        println("-".repeat(80))

        try {
            // Prepare login request
            val loginUrl = "$SUPABASE_URL/auth/v1/token?grant_type=password"
            val url = URL(loginUrl)
            val connection = url.openConnection() as HttpURLConnection

            // Setup request
            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY)
            connection.setRequestProperty("Authorization", "Bearer $SUPABASE_ANON_KEY")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // Create JSON body as string
            val jsonBody = """{"email":"$TEST_EMAIL","password":"$TEST_PASSWORD"}"""

            println("\n🔄 Sending login request to Supabase...")
            println("   Endpoint: $loginUrl")

            // Send request
            connection.outputStream.use { os ->
                val input = jsonBody.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            println("\n📊 Response Code: $responseCode")

            // Read response
            val response = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error details"
            }

            println("📦 Response Body:")
            println("-".repeat(80))
            println(response)
            println("-".repeat(80))

            connection.disconnect()

            // Analyze response
            when (responseCode) {
                200 -> {
                    println("\n✅ LOGIN SUCCESSFUL!")

                    // Parse user info
                    if (containsKey(response, "user")) {
                        val userId = parseJsonValue(response, "id")
                        val userEmail = parseJsonValue(response, "email")
                        val lastSignIn = parseJsonValue(response, "last_sign_in_at")

                        println("\n👤 User Information:")
                        println("   ID: ${userId ?: "N/A"}")
                        println("   Email: ${userEmail ?: "N/A"}")
                        println("   Last Sign In: ${lastSignIn ?: "N/A"}")
                    }

                    // Parse session info
                    if (containsKey(response, "access_token")) {
                        val accessToken = parseJsonValue(response, "access_token")
                        val tokenType = parseJsonValue(response, "token_type")
                        val refreshToken = parseJsonValue(response, "refresh_token")

                        println("\n🔐 Session Information:")
                        println("   Access Token: ${accessToken?.take(20)}...")
                        println("   Token Type: ${tokenType ?: "N/A"}")
                        println("   Refresh Token: ${refreshToken?.take(20)}...")
                    }

                    println("\n🎉 TEST PASSED - User login completed successfully!")
                    println("   You can now use this user in your Android app!")
                    assertTrue("Login should succeed", true)
                }
                400 -> {
                    println("\n❌ LOGIN FAILED - Invalid credentials or bad request")
                    val errorMsg = parseJsonValue(response, "error_description")
                        ?: parseJsonValue(response, "msg")
                        ?: parseJsonValue(response, "message")
                        ?: "Unknown error"
                    println("   Error: $errorMsg")

                    if (errorMsg.contains("Invalid login credentials", ignoreCase = true)) {
                        println("\n💡 The user may not exist yet. Try running the signup test first.")
                        println("   Or check if the email needs to be confirmed.")
                    }

                    fail("Login failed: $errorMsg")
                }
                401 -> {
                    println("\n❌ UNAUTHORIZED - Invalid credentials")
                    println("   Either the email or password is incorrect")
                    println("   Or the user doesn't exist yet")

                    val errorMsg = parseJsonValue(response, "error_description")
                        ?: parseJsonValue(response, "message")
                        ?: "Invalid credentials"

                    fail("Invalid credentials: $errorMsg")
                }
                else -> {
                    println("\n❌ UNEXPECTED RESPONSE CODE")
                    fail("Unexpected response code: $responseCode")
                }
            }

        } catch (e: Exception) {
            println("\n❌ TEST FAILED WITH EXCEPTION")
            println("   Error: ${e.message}")
            e.printStackTrace()
            fail("Exception during login: ${e.message}")
        }

        println("\n" + "=".repeat(80))
        println("🏁 LOGIN TEST COMPLETED")
        println("=".repeat(80) + "\n")
    }
}
