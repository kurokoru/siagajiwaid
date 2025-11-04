package com.siagajiwa.siagajiwa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwa.data.models.QuizData
import com.siagajiwa.siagajiwa.data.models.StressResult
import com.siagajiwa.siagajiwa.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ActivityHistoryUiState(
    val stressHistory: List<StressResult> = emptyList(),
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
            println("📊 [loadQuizHistory] Querying users table for user_id: $userId")
            quizRepository.getAllQuizResults(userId)
                .onSuccess { quizResults ->
                    _uiState.value = _uiState.value.copy(
                        quizHistory = quizResults,
                        isLoading = false
                    )
                    println("📊 [loadQuizHistory] Querying users table for user_id: $quizResults")
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
        // Prevent multiple simultaneous loads
        if (_uiState.value.isLoading) {
            android.util.Log.w("ActivityHistoryVM", "⚠️ Already loading history, skipping duplicate request")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            android.util.Log.d("ActivityHistoryVM", "🔄 Loading all history for user: $userId")

            try {
                // Load both histories in parallel
                val stressJob = launch {
                    android.util.Log.d("ActivityHistoryVM", "📊 Fetching stress history...")
                    quizRepository.getAllStressResults(userId)
                        .onSuccess { stressResults ->
                            android.util.Log.d("ActivityHistoryVM", "✅ Stress history loaded: ${stressResults.size} items")
                            _uiState.value = _uiState.value.copy(
                                stressHistory = stressResults
                            )
                        }
                        .onFailure { exception ->
                            android.util.Log.e("ActivityHistoryVM", "❌ Failed to load stress history: ${exception.message}")
                            exception.printStackTrace()
                        }
                }

                val quizJob = launch {
                    android.util.Log.d("ActivityHistoryVM", "📊 Fetching quiz history...")
                    quizRepository.getAllQuizResults(userId)
                        .onSuccess { quizResults ->
                            android.util.Log.d("ActivityHistoryVM", "✅ Quiz history loaded: ${quizResults.size} items")
                            _uiState.value = _uiState.value.copy(
                                quizHistory = quizResults
                            )
                        }
                        .onFailure { exception ->
                            android.util.Log.e("ActivityHistoryVM", "❌ Failed to load quiz history: ${exception.message}")
                            exception.printStackTrace()
                        }
                }

                // Wait for both to complete
                stressJob.join()
                quizJob.join()

                _uiState.value = _uiState.value.copy(isLoading = false)
                android.util.Log.d("ActivityHistoryVM", "✅ All history loaded successfully")
            } catch (e: Exception) {
                android.util.Log.e("ActivityHistoryVM", "❌ Error loading history: ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load history"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
