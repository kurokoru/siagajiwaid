package com.siagajiwa.siagajiwa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwa.data.QuizQuestion
import com.siagajiwa.siagajiwa.data.Question
import com.siagajiwa.siagajiwa.data.models.PatientQuizQuestion
import com.siagajiwa.siagajiwa.data.models.StressQuizQuestion
import com.siagajiwa.siagajiwa.data.repository.QuizRepository
import com.siagajiwa.siagajiwa.data.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Quiz Content
 */
sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Success(val questions: List<QuizQuestion>) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

/**
 * UI State for Patient Quiz Content
 */
sealed class PatientQuizUiState {
    object Loading : PatientQuizUiState()
    data class Success(val questions: List<Question>) : PatientQuizUiState()
    data class Error(val message: String) : PatientQuizUiState()
}

/**
 * ViewModel for managing quiz content from Supabase
 * Handles: Stress Quiz and Patient Quiz
 */
class QuizViewModel : ViewModel() {
    private val repository = QuizRepository()

    // State flows for each quiz type
    private val _stressQuizState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val stressQuizState: StateFlow<QuizUiState> = _stressQuizState.asStateFlow()

    private val _patientQuizState = MutableStateFlow<PatientQuizUiState>(PatientQuizUiState.Loading)
    val patientQuizState: StateFlow<PatientQuizUiState> = _patientQuizState.asStateFlow()

    /**
     * Load stress quiz questions from Supabase
     */
    fun loadStressQuiz() {
        viewModelScope.launch {
            _stressQuizState.value = QuizUiState.Loading
            val result = repository.getStressQuiz()

            _stressQuizState.value = result.fold(
                onSuccess = { questions ->
                    if (questions.isEmpty()) {
                        QuizUiState.Error("No quiz questions found in database")
                    } else {
                        // Convert Supabase questions to QuizQuestion format
                        val quizQuestions = convertStressQuizToQuestions(questions)
                        QuizUiState.Success(quizQuestions)
                    }
                },
                onFailure = { exception ->
                    QuizUiState.Error(exception.message ?: "Failed to load quiz")
                }
            )
        }
    }

    /**
     * Load patient quiz questions from Supabase
     */
    fun loadPatientQuiz() {
        viewModelScope.launch {
            _patientQuizState.value = PatientQuizUiState.Loading
            val result = repository.getPatientQuiz()

            _patientQuizState.value = result.fold(
                onSuccess = { questions ->
                    if (questions.isEmpty()) {
                        PatientQuizUiState.Error("No quiz questions found in database")
                    } else {
                        // Convert Supabase questions to Question format
                        val quizQuestions = convertPatientQuizToQuestions(questions)
                        PatientQuizUiState.Success(quizQuestions)
                    }
                },
                onFailure = { exception ->
                    PatientQuizUiState.Error(exception.message ?: "Failed to load quiz")
                }
            )
        }
    }

    /**
     * Convert Supabase stress quiz questions to QuizQuestion list
     * Parses pipe-separated answer options from answer_option column
     */
    private fun convertStressQuizToQuestions(questions: List<StressQuizQuestion>): List<QuizQuestion> {
        return questions
            .sortedBy { it.order ?: it.questionNumber ?: 0 }
            .map { q ->
                QuizQuestion(
                    id = q.questionNumber ?: 0,
                    text = q.questionText ?: "",
                    options = q.getAnswerOptions(), // Parse pipe-separated options
                    correctAnswerIndex = q.correctAnswer ?: 0 // Store correct answer index
                )
            }
    }

    /**
     * Convert Supabase patient quiz questions to Question list
     */
    private fun convertPatientQuizToQuestions(questions: List<PatientQuizQuestion>): List<Question> {
        return questions
            .sortedBy { it.order ?: it.questionNumber ?: 0 }
            .map { q ->
                Question(
                    id = q.questionNumber ?: 0,
                    text = q.questionText ?: ""
                )
            }
    }

    /**
     * Retry loading quiz based on the type
     */
    fun retryLoading(quizType: QuizType) {
        when (quizType) {
            QuizType.STRESS -> loadStressQuiz()
            QuizType.PATIENT -> loadPatientQuiz()
        }
    }

    /**
     * Submit quiz results with answers
     * Calculates score based on correct answers
     */
    fun submitQuiz(
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

                // Calculate score
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
                val result = repository.submitQuizResult(quizData)

                result.fold(
                    onSuccess = {
                        onSuccess(correctAnswers, totalQuestions)
                    },
                    onFailure = { exception ->
                        onError(exception.message ?: "Failed to submit quiz")
                    }
                )
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            }
        }
    }
}

/**
 * Enum to identify quiz type for retry operations
 */
enum class QuizType {
    STRESS,
    PATIENT
}
