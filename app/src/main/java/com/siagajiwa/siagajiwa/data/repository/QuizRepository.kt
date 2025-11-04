package com.siagajiwa.siagajiwa.data.repository

import com.siagajiwa.siagajiwa.data.SupabaseClient
import com.siagajiwa.siagajiwa.data.models.PatientQuizQuestion
import com.siagajiwa.siagajiwa.data.models.QuizData
import com.siagajiwa.siagajiwa.data.models.StressQuizQuestion
import com.siagajiwa.siagajiwa.data.models.StressResult
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

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
                // Generate UUID for the result if not already present
                val quizDataWithId = if (quizData.id == null) {
                    quizData.copy(id = UUID.randomUUID().toString())
                } else {
                    quizData
                }

                // Insert quiz result
                database.from("quiz_results").insert(quizDataWithId)

                // Update user's knowledge score
                database.from("profiles")
                    .update(
                        mapOf(
                            "knowledge_score" to quizDataWithId.quizScore,
                            "knowledge_percentage" to quizDataWithId.percentage
                        )
                    ) {
                        filter {
                            eq("id", quizDataWithId.userId)
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
                        order(column = "created_at", order = Order.DESCENDING)
                    }
                    .decodeList<QuizData>()

                Result.success(quizResults)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Insert stress test result into stress_results table
     *
     * @param userId User UUID
     * @param stressLevel Stress level enum: "Rendah", "Sedang", or "Tinggi"
     * @param stressScore Total stress score (0-40)
     * @return Result<String> with the inserted record ID
     */
    suspend fun insertStressResult(
        userId: String,
        stressLevel: String,
        stressScore: Int
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Generate UUID for the result
                val resultId = UUID.randomUUID().toString()

                // Create StressResult object
                val stressResult = StressResult(
                    id = resultId,
                    userId = userId,
                    stressLevel = stressLevel,
                    stressScore = stressScore
                )

                // Insert into database
                database.from("stress_results").insert(stressResult)

                Result.success(resultId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get the latest stress test result for a user
     */
    suspend fun getLatestStressResult(userId: String): Result<StressResult?> {
        return withContext(Dispatchers.IO) {
            try {
                val results = database.from("stress_results")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                        order(column = "test_date", order = Order.DESCENDING)
                        limit(count = 1)
                    }
                    .decodeList<StressResult>()

                Result.success(results.firstOrNull())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get all stress test results for a user
     */
    suspend fun getAllStressResults(userId: String): Result<List<StressResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val results = database.from("stress_results")
                    .select() {
                        filter {
                            eq("user_id", userId)
                        }
                        order(column = "test_date", order = Order.DESCENDING)
                    }
                    .decodeList<StressResult>()

                Result.success(results)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
