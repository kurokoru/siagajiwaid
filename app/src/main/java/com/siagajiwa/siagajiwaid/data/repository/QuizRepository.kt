package com.siagajiwa.siagajiwaid.data.repository

import com.siagajiwa.siagajiwaid.data.SupabaseClient
import com.siagajiwa.siagajiwaid.data.models.PatientQuizQuestion
import com.siagajiwa.siagajiwaid.data.models.QuizData
import com.siagajiwa.siagajiwaid.data.models.StressQuizQuestion
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository {
    private val database = SupabaseClient.database

    /**
     * Fetch stress quiz questions from stress_quiz table
     * Ordered by 'order' column in ascending order
     */
    suspend fun getStressQuiz(): Result<List<StressQuizQuestion>> {
        return withContext(Dispatchers.IO) {
            try {
                val questions = database.from("stress_quiz")
                    .select() {
                       /* order(column = "order", order = Order.ASCENDING)*/
                    }
                    .decodeList<StressQuizQuestion>()

                Result.success(questions)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Fetch patient care quiz questions from perawatan_quiz table
     * Ordered by 'order' column in ascending order
     */
    suspend fun getPatientQuiz(): Result<List<PatientQuizQuestion>> {
        return withContext(Dispatchers.IO) {
            try {
                val questions = database.from("perawatan_quiz")
                    .select() {
                     /*   order(column = "order", order = Order.ASCENDING)*/
                    }
                    .decodeList<PatientQuizQuestion>()

                Result.success(questions)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Submit quiz result to quiz_results table
     * Also updates user's knowledge score and percentage
     */
    suspend fun submitQuizResult(quizData: QuizData): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Insert quiz result
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

    /**
     * Get the latest quiz result for a user
     */
    suspend fun getLatestQuizResult(userId: String): Result<QuizData?> {
        return withContext(Dispatchers.IO) {
            try {
                val quizResults = database.from("quiz_results")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                        order(column = "quiz_date", order = Order.DESCENDING)
                        limit(count = 1)
                    }
                    .decodeList<QuizData>()

                Result.success(quizResults.firstOrNull())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get all quiz results for a user
     */
    suspend fun getAllQuizResults(userId: String): Result<List<QuizData>> {
        return withContext(Dispatchers.IO) {
            try {
                val quizResults = database.from("quiz_results")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                        order(column = "quiz_date", order = Order.DESCENDING)
                    }
                    .decodeList<QuizData>()

                Result.success(quizResults)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get the latest stress result for a user from stress_results table
     */
    suspend fun getLatestStressResult(userId: String): Result<com.siagajiwa.siagajiwaid.data.models.StressData?> {
        return withContext(Dispatchers.IO) {
            try {
                val stressResults = database.from("stress_results")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                        order(column = "test_date", order = Order.DESCENDING)
                        limit(count = 1)
                    }
                    .decodeList<com.siagajiwa.siagajiwaid.data.models.StressData>()

                Result.success(stressResults.firstOrNull())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get all stress results for a user from stress_results table
     */
    suspend fun getAllStressResults(userId: String): Result<List<com.siagajiwa.siagajiwaid.data.models.StressData>> {
        return withContext(Dispatchers.IO) {
            try {
                val stressResults = database.from("stress_results")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                        order(column = "test_date", order = Order.DESCENDING)
                    }
                    .decodeList<com.siagajiwa.siagajiwaid.data.models.StressData>()

                Result.success(stressResults)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
