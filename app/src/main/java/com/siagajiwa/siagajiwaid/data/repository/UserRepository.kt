package com.siagajiwa.siagajiwaid.data.repository

import com.siagajiwa.siagajiwaid.data.SupabaseClient
import com.siagajiwa.siagajiwaid.data.models.User
import com.siagajiwa.siagajiwaid.data.models.StressData
import com.siagajiwa.siagajiwaid.data.models.QuizData
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
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
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                val currentUser = auth.currentUserOrNull()
                    ?: throw Exception("Signup successful but user info not available")

                // Create user profile
                createUserProfile(currentUser.id, email, fullName)

                Result.success(currentUser)
            } catch (e: Exception) {
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
                database.from("users").insert(
                    mapOf(
                        "id" to userId,
                        "email" to email,
                        "full_name" to fullName
                    )
                )
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUserProfile(userId: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                println("📊 [UserRepository] Querying users table for user_id: $userId")
                val user = database.from("users")
                    .select() {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingle<User>()

                println("✅ [UserRepository] User profile found: ${user.email}")
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
                database.from("users")
                    .update(
                        mapOf(
                            "full_name" to user.fullName,
                            "avatar_url" to user.avatarUrl,
                            "updated_at" to "now()"
                        )
                    ) {
                        filter {
                            eq("id", user.id)
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
