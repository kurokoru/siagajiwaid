package com.siagajiwa.siagajiwaid.data

/**
 * Mock user data for development and testing purposes
 */
object MockUserData {

    // List of mock users with different roles
    val mockUsers = listOf(
        User(
            id = "1",
            name = "Melissa Jenner",
            email = "melissa.jenner@example.com",
            password = "password123",
            role = UserRole.CAREGIVER,
            phoneNumber = "+62 812-3456-7890",
            dateOfBirth = "1985-05-15",
            address = "Jakarta, Indonesia"
        ),
        User(
            id = "2",
            name = "John Caregiver",
            email = "john@caregiver.com",
            password = "caregiver123",
            role = UserRole.CAREGIVER,
            phoneNumber = "+62 813-4567-8901",
            dateOfBirth = "1990-08-20",
            address = "Bandung, Indonesia"
        ),
        User(
            id = "3",
            name = "Dr. Sarah Smith",
            email = "sarah.smith@hospital.com",
            password = "doctor123",
            role = UserRole.HEALTHCARE_PROFESSIONAL,
            phoneNumber = "+62 821-5678-9012",
            dateOfBirth = "1980-03-10",
            address = "Surabaya, Indonesia"
        ),
        User(
            id = "4",
            name = "Admin User",
            email = "admin@siagajiwa.com",
            password = "admin123",
            role = UserRole.ADMIN,
            phoneNumber = "+62 822-6789-0123",
            dateOfBirth = "1988-12-05",
            address = "Jakarta, Indonesia"
        ),
        User(
            id = "5",
            name = "Maria Garcia",
            email = "maria@example.com",
            password = "maria123",
            role = UserRole.CAREGIVER,
            phoneNumber = "+62 823-7890-1234",
            dateOfBirth = "1992-06-25",
            address = "Yogyakarta, Indonesia"
        ),
        User(
        id = "6",
        name = "Maria Garcia",
        email = "m@id.id",
        password = "12345",
        role = UserRole.CAREGIVER,
        phoneNumber = "+62 823-7890-1234",
        dateOfBirth = "1992-06-25",
        address = "Yogyakarta, Indonesia"
    )
    )

    // Quick access test credentials
    object TestCredentials {
        const val DEFAULT_EMAIL = "melissa.jenner@example.com"
        const val DEFAULT_PASSWORD = "password123"

        const val CAREGIVER_EMAIL = "john@caregiver.com"
        const val CAREGIVER_PASSWORD = "caregiver123"

        const val DOCTOR_EMAIL = "sarah.smith@hospital.com"
        const val DOCTOR_PASSWORD = "doctor123"

        const val ADMIN_EMAIL = "admin@siagajiwa.com"
        const val ADMIN_PASSWORD = "admin123"
    }

    /**
     * Find user by email
     */
    fun findUserByEmail(email: String): User? {
        return mockUsers.find { it.email.equals(email, ignoreCase = true) }
    }

    /**
     * Validate login credentials
     */
    fun validateCredentials(email: String, password: String): User? {
        return mockUsers.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    /**
     * Check if email exists
     */
    fun emailExists(email: String): Boolean {
        return mockUsers.any { it.email.equals(email, ignoreCase = true) }
    }
}
