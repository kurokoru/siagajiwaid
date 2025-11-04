package com.siagajiwa.siagajiwa.data

import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Authentication Repository for handling login, signup, and authentication logic
 * Currently uses mock data for development purposes
 */
class AuthRepository {

    private var currentUser: User? = null
    private var authToken: String? = null

    /**
     * Simulate login with network delay
     * @param email User email
     * @param password User password
     * @return LoginResponse with success status and user data
     */
    suspend fun login(email: String, password: String): LoginResponse {
        // Simulate network delay
        delay(1000)

        // Validate inputs
        if (email.isBlank() || password.isBlank()) {
            return LoginResponse(
                success = false,
                message = "Email dan password tidak boleh kosong"
            )
        }

        // Validate email format
        if (!isValidEmail(email)) {
            return LoginResponse(
                success = false,
                message = "Format email tidak valid"
            )
        }

        // Check credentials
        val user = MockUserData.validateCredentials(email, password)

        return if (user != null) {
            // Generate mock token
            val token = generateAuthToken()

            // Store current user and token
            currentUser = user
            authToken = token

            LoginResponse(
                success = true,
                user = user,
                token = token,
                message = "Login berhasil"
            )
        } else {
            LoginResponse(
                success = false,
                message = "Email atau password salah"
            )
        }
    }

    /**
     * Simulate signup/registration
     */
    suspend fun signup(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): LoginResponse {
        // Simulate network delay
        delay(1000)

        // Validate inputs
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return LoginResponse(
                success = false,
                message = "Semua field harus diisi"
            )
        }

        if (!isValidEmail(email)) {
            return LoginResponse(
                success = false,
                message = "Format email tidak valid"
            )
        }

        if (password.length < 6) {
            return LoginResponse(
                success = false,
                message = "Password minimal 6 karakter"
            )
        }

        if (password != confirmPassword) {
            return LoginResponse(
                success = false,
                message = "Password dan konfirmasi password tidak sama"
            )
        }

        // Check if email already exists
        if (MockUserData.emailExists(email)) {
            return LoginResponse(
                success = false,
                message = "Email sudah terdaftar"
            )
        }

        // Create new user (in real app, this would be saved to database)
        val newUser = User(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            password = password,
            role = UserRole.CAREGIVER
        )

        val token = generateAuthToken()
        currentUser = newUser
        authToken = token

        return LoginResponse(
            success = true,
            user = newUser,
            token = token,
            message = "Registrasi berhasil"
        )
    }

    /**
     * Logout current user
     */
    fun logout() {
        currentUser = null
        authToken = null
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return currentUser != null && authToken != null
    }

    /**
     * Get current logged-in user
     */
    fun getCurrentUser(): User? {
        return currentUser
    }

    /**
     * Get current auth token
     */
    fun getAuthToken(): String? {
        return authToken
    }

    /**
     * Validate email format
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Generate mock authentication token
     */
    private fun generateAuthToken(): String {
        return "mock_token_${UUID.randomUUID()}"
    }

    /**
     * Reset password (mock implementation)
     */
    suspend fun resetPassword(email: String): LoginResponse {
        delay(1000)

        if (!isValidEmail(email)) {
            return LoginResponse(
                success = false,
                message = "Format email tidak valid"
            )
        }

        val user = MockUserData.findUserByEmail(email)

        return if (user != null) {
            LoginResponse(
                success = true,
                message = "Link reset password telah dikirim ke email Anda"
            )
        } else {
            LoginResponse(
                success = false,
                message = "Email tidak ditemukan"
            )
        }
    }

    companion object {
        // Singleton instance
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
        }
    }
}
