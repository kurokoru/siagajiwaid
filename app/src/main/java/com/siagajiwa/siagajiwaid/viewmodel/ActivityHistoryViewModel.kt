package com.siagajiwa.siagajiwaid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwaid.data.models.QuizData
import com.siagajiwa.siagajiwaid.data.models.StressData
import com.siagajiwa.siagajiwaid.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ActivityHistoryUiState(
    val stressHistory: List<StressData> = emptyList(),
    val quizHistory: List<QuizData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ActivityHistoryViewModel : ViewModel() {
    private val quizRepository = QuizRepository()

    private val _uiState = MutableStateFlow(ActivityHistoryUiState())
    val uiState: StateFlow<ActivityHistoryUiState> = _uiState.asStateFlow()

    /**
     * Load all stress results for a user
     */
    fun loadStressHistory(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            quizRepository.getAllStressResults(userId)
                .onSuccess { stressResults ->
                    _uiState.value = _uiState.value.copy(
                        stressHistory = stressResults,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load stress history"
                    )
                }
        }
    }

    /**
     * Load all quiz results for a user
     */
    fun loadQuizHistory(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            quizRepository.getAllQuizResults(userId)
                .onSuccess { quizResults ->
                    _uiState.value = _uiState.value.copy(
                        quizHistory = quizResults,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load quiz history"
                    )
                }
        }
    }

    /**
     * Load both stress and quiz history for a user
     */
    fun loadAllHistory(userId: String) {
        loadStressHistory(userId)
        loadQuizHistory(userId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
