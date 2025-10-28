package com.siagajiwa.siagajiwaid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwaid.data.QuizQuestion
import com.siagajiwa.siagajiwaid.data.models.StressQuizQuestion
import com.siagajiwa.siagajiwaid.data.repository.QuizRepository
import com.siagajiwa.siagajiwaid.data.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Stress Quiz
 */
sealed class StressQuizUiState {
    object Loading : StressQuizUiState()
    data class Success(val questions: List<QuizQuestion>) : StressQuizUiState()
    data class Error(val message: String) : StressQuizUiState()
}

/**
 * ViewModel for managing Stress Assessment Quiz
 * Handles stress level assessment with 5-point rating scale (0-4)
 */
class StressQuizViewModel : ViewModel() {
    private val repository = QuizRepository()

    private val _quizState = MutableStateFlow<StressQuizUiState>(StressQuizUiState.Loading)
    val quizState: StateFlow<StressQuizUiState> = _quizState.asStateFlow()

    /**
     * Load stress quiz questions from Supabase
     */
    fun loadQuiz() {
        viewModelScope.launch {
            _quizState.value = StressQuizUiState.Loading
            val result = repository.getStressQuiz()

            _quizState.value = result.fold(
                onSuccess = { questions ->
                    if (questions.isEmpty()) {
                        StressQuizUiState.Error("No quiz questions found in database")
                    } else {
                        val quizQuestions = convertToQuizQuestions(questions)
                        StressQuizUiState.Success(quizQuestions)
                    }
                },
                onFailure = { exception ->
                    StressQuizUiState.Error(exception.message ?: "Failed to load stress quiz")
                }
            )
        }
    }

    /**
     * Convert Supabase stress quiz questions to QuizQuestion list
     * For stress assessment, we don't use the answer_option field
     * Users select from 0-4 rating scale instead
     */
    private fun convertToQuizQuestions(questions: List<StressQuizQuestion>): List<QuizQuestion> {
        return questions
            .sortedBy { it.order ?: it.questionNumber ?: 0 }
            .map { q ->
                QuizQuestion(
                    id = q.questionNumber ?: 0,
                    text = q.questionText ?: "",
                    options = emptyList(), // Stress quiz uses rating scale, not text options
                    correctAnswerIndex = null // No correct answer for stress assessment
                )
            }
    }

    /**
     * Submit stress quiz results
     * Calculates total stress score (sum of all ratings)
     */
    fun submitStressQuiz(
        answers: Map<Int, String>,
        questions: List<QuizQuestion>,
        onSuccess: (Int, Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = SupabaseClient.auth.currentUserOrNull()?.id
                if (userId == null) {
                    onError("User not logged in")
                    return@launch
                }

                // Calculate total stress score (sum of all ratings 0-4)
                var totalScore = 0
                answers.forEach { (_, value) ->
                    totalScore += value.toIntOrNull() ?: 0
                }

                val totalQuestions = questions.size
                val maxPossibleScore = totalQuestions * 4 // Max score if all answers are 4
                val percentage = if (maxPossibleScore > 0) {
                    (totalScore * 100) / maxPossibleScore
                } else 0

                // Create stress quiz data
                val stressData = com.siagajiwa.siagajiwaid.data.models.QuizData(
                    userId = userId,
                    quizScore = totalScore,
                    totalQuestions = totalQuestions,
                    percentage = percentage
                )

                // Submit to repository
                val result = repository.submitQuizResult(stressData)

                result.fold(
                    onSuccess = {
                        onSuccess(totalScore, maxPossibleScore)
                    },
                    onFailure = { exception ->
                        onError(exception.message ?: "Failed to submit stress quiz")
                    }
                )
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            }
        }
    }

    /**
     * Retry loading quiz
     */
    fun retryLoading() {
        loadQuiz()
    }
}
