package com.siagajiwa.siagajiwaid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwaid.data.QuizData
import com.siagajiwa.siagajiwaid.data.QuizPage
import com.siagajiwa.siagajiwaid.data.QuizQuestion
import com.siagajiwa.siagajiwaid.data.Question
import com.siagajiwa.siagajiwaid.data.QuestionnaireData
import com.siagajiwa.siagajiwaid.data.QuestionnairePage
import com.siagajiwa.siagajiwaid.data.models.PatientQuizQuestion
import com.siagajiwa.siagajiwaid.data.models.StressQuizQuestion
import com.siagajiwa.siagajiwaid.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Quiz Content
 */
sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Success(val pages: List<QuizPage>) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

/**
 * UI State for Patient Quiz Content
 */
sealed class PatientQuizUiState {
    object Loading : PatientQuizUiState()
    data class Success(val pages: List<QuestionnairePage>) : PatientQuizUiState()
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
                        // Fallback to local data if no data in Supabase
                        QuizUiState.Success(QuizData.pages)
                    } else {
                        // Convert Supabase questions to QuizPage format
                        val pages = convertStressQuizToPages(questions)
                        QuizUiState.Success(pages)
                    }
                },
                onFailure = { exception ->
                    // Fallback to local data on error
                    QuizUiState.Success(QuizData.pages)
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
                        // Fallback to local data if no data in Supabase
                        PatientQuizUiState.Success(QuestionnaireData.pages)
                    } else {
                        // Convert Supabase questions to QuestionnairePage format
                        val pages = convertPatientQuizToPages(questions)
                        PatientQuizUiState.Success(pages)
                    }
                },
                onFailure = { exception ->
                    // Fallback to local data on error
                    PatientQuizUiState.Success(QuestionnaireData.pages)
                }
            )
        }
    }

    /**
     * Convert Supabase stress quiz questions to QuizPage format
     */
    private fun convertStressQuizToPages(questions: List<StressQuizQuestion>): List<QuizPage> {
        return questions
            .groupBy { it.pageNumber }
            .map { (pageNum, pageQuestions) ->
                QuizPage(
                    pageNumber = pageNum,
                    questions = pageQuestions.map { q ->
                        QuizQuestion(
                            id = q.questionNumber,
                            text = q.questionText,
                            options = listOf(q.option1, q.option2)
                        )
                    }
                )
            }
            .sortedBy { it.pageNumber }
    }

    /**
     * Convert Supabase patient quiz questions to QuestionnairePage format
     */
    private fun convertPatientQuizToPages(questions: List<PatientQuizQuestion>): List<QuestionnairePage> {
        return questions
            .groupBy { it.pageNumber }
            .map { (pageNum, pageQuestions) ->
                QuestionnairePage(
                    pageNumber = pageNum,
                    questions = pageQuestions.map { q ->
                        Question(
                            id = q.questionNumber,
                            text = q.questionText
                        )
                    }
                )
            }
            .sortedBy { it.pageNumber }
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
}

/**
 * Enum to identify quiz type for retry operations
 */
enum class QuizType {
    STRESS,
    PATIENT
}
