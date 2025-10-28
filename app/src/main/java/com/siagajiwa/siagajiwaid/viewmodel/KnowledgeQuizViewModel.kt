package com.siagajiwa.siagajiwaid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwaid.data.QuizQuestion
import com.siagajiwa.siagajiwaid.data.models.PatientQuizQuestion
import com.siagajiwa.siagajiwaid.data.repository.QuizRepository
import com.siagajiwa.siagajiwaid.data.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Knowledge Quiz
 */
sealed class KnowledgeQuizUiState {
    object Loading : KnowledgeQuizUiState()
    data class Success(val questions: List<QuizQuestion>) : KnowledgeQuizUiState()
    data class Error(val message: String) : KnowledgeQuizUiState()
}

/**
 * ViewModel for managing Patient Knowledge Quiz
 * Handles multiple-choice knowledge assessment with 4 options
 */
class KnowledgeQuizViewModel : ViewModel() {
    private val repository = QuizRepository()

    private val _quizState = MutableStateFlow<KnowledgeQuizUiState>(KnowledgeQuizUiState.Loading)
    val quizState: StateFlow<KnowledgeQuizUiState> = _quizState.asStateFlow()

    /**
     * Load knowledge quiz questions from Supabase
     */
    fun loadQuiz() {
        viewModelScope.launch {
            _quizState.value = KnowledgeQuizUiState.Loading
            val result = repository.getPatientQuiz()

            _quizState.value = result.fold(
                onSuccess = { questions ->
                    if (questions.isEmpty()) {
                        KnowledgeQuizUiState.Error("No quiz questions found in database")
                    } else {
                        val quizQuestions = convertToQuizQuestions(questions)
                        KnowledgeQuizUiState.Success(quizQuestions)
                    }
                },
                onFailure = { exception ->
                    KnowledgeQuizUiState.Error(exception.message ?: "Failed to load knowledge quiz")
                }
            )
        }
    }

    /**
     * Convert Supabase patient quiz questions to QuizQuestion list
     * Parses pipe-separated answer options from answer_option column
     */
    private fun convertToQuizQuestions(questions: List<PatientQuizQuestion>): List<QuizQuestion> {
        return questions
            .sortedBy { it.order ?: it.questionNumber ?: 0 }
            .map { q ->
                QuizQuestion(
                    id = q.questionNumber ?: 0,
                    text = q.questionText ?: "",
                    options = parseAnswerOptions(q.answerOption),
                    correctAnswerIndex = q.correctAnswer ?: 0
                )
            }
    }

    /**
     * Parse pipe-separated answer options
     */
    private fun parseAnswerOptions(answerOption: String?): List<String> {
        return answerOption?.split("|")?.map { it.trim() } ?: emptyList()
    }

    /**
     * Submit knowledge quiz results
     * Calculates score based on correct answers
     */
    fun submitKnowledgeQuiz(
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

                // Calculate score based on correct answers
                var correctAnswers = 0
                questions.forEach { question ->
                    val userAnswer = answers[question.id]
                    if (question.correctAnswerIndex != null && userAnswer != null) {
                        val correctOption = question.options.getOrNull(question.correctAnswerIndex)
                        if (userAnswer == correctOption) {
                            correctAnswers++
                        }
                    }
                }

                val totalQuestions = questions.size
                val percentage = if (totalQuestions > 0) {
                    (correctAnswers * 100) / totalQuestions
                } else 0

                // Create quiz data
                val quizData = com.siagajiwa.siagajiwaid.data.models.QuizData(
                    userId = userId,
                    quizScore = correctAnswers,
                    totalQuestions = totalQuestions,
                    percentage = percentage
                )

                // Submit to repository
                val result = repository.submitQuizResult(quizData)

                result.fold(
                    onSuccess = {
                        onSuccess(correctAnswers, totalQuestions)
                    },
                    onFailure = { exception ->
                        onError(exception.message ?: "Failed to submit knowledge quiz")
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
