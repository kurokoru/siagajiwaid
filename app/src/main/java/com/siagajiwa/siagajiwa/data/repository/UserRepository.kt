package com.siagajiwa.siagajiwa.data.repository

import com.siagajiwa.siagajiwa.data.SupabaseClient
import com.siagajiwa.siagajiwa.data.models.User
import com.siagajiwa.siagajiwa.data.models.StressData
import com.siagajiwa.siagajiwa.data.models.QuizData
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private val supabase = SupabaseClient.client
    private val auth = SupabaseClient.auth
    private val database = SupabaseClient.database

    // Authentication
    suspend fun signInWithEmail(email: String, password: String): Result<UserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                val currentUser = auth.currentUserOrNull()
                    ?: throw Exception("Login successful but user info not available")
                Result.success(currentUser)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String
    ): Result<UserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔐 [UserRepository] Starting signup for email: $email")

                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                val currentUser = auth.currentUserOrNull()
                    ?: throw Exception("Signup successful but user info not available")

                println("✅ [UserRepository] Auth signup successful, user ID: ${currentUser.id}")

                // Create user profile
                val profileResult = createUserProfile(currentUser.id, email, fullName)
                if (profileResult.isFailure) {
                    println("⚠️ [UserRepository] Profile creation failed but auth succeeded")
                    println("   User can still login but profile needs to be created manually")
                    println("   Error: ${profileResult.exceptionOrNull()?.message}")
                }

                Result.success(currentUser)
            } catch (e: Exception) {
                println("❌ [UserRepository] Signup failed: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun signInWithGoogle(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                auth.signInWith(Google)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun signOut(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                auth.signOut()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun getCurrentUser(): UserInfo? {
        return auth.currentUserOrNull()
    }

    // User Profile
    suspend fun createUserProfile(
        userId: String,
        email: String,
        fullName: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                println("📝 [UserRepository] Creating profile for user: $userId")
                println("   Email: $email")
                println("   Full Name: $fullName")

                database.from("profiles").insert(
                    mapOf(
                        "user_id" to userId,
                        "email" to email,
                        "full_name" to fullName
                    )
                )

                println("✅ [UserRepository] Profile created successfully for user: $userId")
                Result.success(Unit)
            } catch (e: Exception) {
                println("❌ [UserRepository] Failed to create profile: ${e.message}")
                println("   User ID: $userId")
                println("   Error details: ${e.stackTraceToString()}")
                Result.failure(e)
            }
        }
    }

    suspend fun getUserProfile(userId: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                println("📊 [UserRepository] Querying profiles table for user_id: $userId")
                val user = database.from("profiles")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeSingle<User>()

                println("✅ [UserRepository] User profiles found: ${user.email}")
                Result.success(user)
            } catch (e: Exception) {
                println("❌ [UserRepository] Failed to get user profile: ${e.message}")
                println("   This likely means the user exists in auth.users but not in public.users table")
                Result.failure(e)
            }
        }
    }

    suspend fun updateUserProfile(user: User): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                database.from("profiles")
                    .update(
                        mapOf(
                            "full_name" to user.fullName,
                            "avatar_url" to user.avatarUrl,
                            "updated_at" to "now()"
                        )
                    ) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Stress Test Data
    suspend fun saveStressTest(stressData: StressData): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Save to stress_results table
                database.from("stress_results").insert(stressData)

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getLatestStressTest(userId: String): Result<StressData?> {
        return withContext(Dispatchers.IO) {
            try {
                val stressTests = database.from("stress_tests")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                        order(column = "test_date", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                        limit(count = 1)
                    }
                    .decodeList<StressData>()

                Result.success(stressTests.firstOrNull())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Quiz Data
    suspend fun saveQuizResult(quizData: QuizData): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                database.from("quiz_results").insert(quizData)

                // Update user's knowledge score
                database.from("users")
                    .update(
                        mapOf(
                            "knowledge_score" to quizData.quizScore,
                            "knowledge_percentage" to quizData.percentage
                        )
                    ) {
                        filter {
                            eq("id", quizData.userId)
                        }
                    }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getLatestQuizResult(userId: String): Result<QuizData?> {
        return withContext(Dispatchers.IO) {
            try {
                val quizResults = database.from("quiz_results")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                        order(column = "quiz_date", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                        limit(count = 1)
                    }
                    .decodeList<QuizData>()

                Result.success(quizResults.firstOrNull())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
