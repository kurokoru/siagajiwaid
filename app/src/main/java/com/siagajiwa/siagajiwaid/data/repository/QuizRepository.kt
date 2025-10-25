package com.siagajiwa.siagajiwaid.data.repository

import com.siagajiwa.siagajiwaid.data.SupabaseClient
import com.siagajiwa.siagajiwaid.data.models.PatientQuizQuestion
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
                        order(column = "order", order = Order.ASCENDING)
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
                        order(column = "order", order = Order.ASCENDING)
                    }
                    .decodeList<PatientQuizQuestion>()

                Result.success(questions)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
