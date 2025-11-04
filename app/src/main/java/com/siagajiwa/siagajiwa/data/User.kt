package com.siagajiwa.siagajiwa.data

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole = UserRole.CAREGIVER,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val address: String? = null
)

enum class UserRole {
    CAREGIVER,
    ADMIN,
    HEALTHCARE_PROFESSIONAL
}

data class LoginCredentials(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val user: User? = null,
    val token: String? = null,
    val message: String? = null
)
