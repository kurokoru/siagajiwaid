package com.siagajiwa.siagajiwa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwa.data.QuizQuestion
import com.siagajiwa.siagajiwa.data.models.PatientQuizQuestion
import com.siagajiwa.siagajiwa.data.repository.QuizRepository
import com.siagajiwa.siagajiwa.data.SupabaseClient
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

    // Temporary storage for quiz data before submission
    private var storedUserId: String? = null
    private var storedAnswers: Map<Int, String>? = null
    private var storedQuestions: List<QuizQuestion>? = null

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
        userId: String?,  // Accept userId as parameter instead of getting from auth
        answers: Map<Int, String>,
        questions: List<QuizQuestion>,
        onSuccess: (Int, Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                android.util.Log.d("KnowledgeQuizVM", "Starting quiz submission")
                android.util.Log.d("KnowledgeQuizVM", "User ID from parameter: $userId")

                if (userId == null) {
                    android.util.Log.e("KnowledgeQuizVM", "User ID is null - not logged in")
                    onError("User not logged in. Please sign in again.")
                    return@launch
                }

                // Also check SupabaseClient for debugging
                val currentUser = SupabaseClient.auth.currentUserOrNull()
                android.util.Log.d("KnowledgeQuizVM", "Current user from SupabaseClient: ${currentUser?.email}")

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
                val quizData = com.siagajiwa.siagajiwa.data.models.QuizData(
                    userId = userId,
                    quizScore = correctAnswers,
                    totalQuestions = totalQuestions,
                    percentage = percentage
                )

                // Submit to repository
                android.util.Log.d("KnowledgeQuizVM", "Submitting quiz result to database")
                android.util.Log.d("KnowledgeQuizVM", "Score: $correctAnswers/$totalQuestions ($percentage%)")

                val result = repository.submitQuizResult(quizData)

                result.fold(
                    onSuccess = {
                        android.util.Log.d("KnowledgeQuizVM", "Quiz result submitted successfully")
                        onSuccess(correctAnswers, totalQuestions)
                    },
                    onFailure = { exception ->
                        android.util.Log.e("KnowledgeQuizVM", "Failed to submit quiz: ${exception.message}")
                        exception.printStackTrace()
                        onError(exception.message ?: "Failed to submit knowledge quiz")
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("KnowledgeQuizVM", "Exception during quiz submission: ${e.message}")
                e.printStackTrace()
                onError(e.message ?: "An error occurred")
            }
        }
    }

    /**
     * Store quiz data for later submission (called from Submit button)
     */
    fun storeQuizData(
        userId: String?,
        answers: Map<Int, String>,
        questions: List<QuizQuestion>
    ) {
        storedUserId = userId
        storedAnswers = answers
        storedQuestions = questions
        android.util.Log.d("KnowledgeQuizVM", "Quiz data stored for later submission")
    }

    /**
     * Submit stored quiz data (called from Simpan button in result screen)
     */
    fun submitStoredQuiz(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (storedUserId == null || storedAnswers == null || storedQuestions == null) {
            android.util.Log.e("KnowledgeQuizVM", "No stored quiz data found")
            onError("No quiz data to submit")
            return
        }

        submitKnowledgeQuiz(
            userId = storedUserId,
            answers = storedAnswers!!,
            questions = storedQuestions!!,
            onSuccess = { _, _ ->
                // Clear stored data after successful submission
                storedUserId = null
                storedAnswers = null
                storedQuestions = null
                onSuccess()
            },
            onError = onError
        )
    }

    /**
     * Retry loading quiz
     */
    fun retryLoading() {
        loadQuiz()
    }
}
