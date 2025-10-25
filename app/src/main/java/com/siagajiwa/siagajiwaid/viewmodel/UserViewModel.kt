package com.siagajiwa.siagajiwaid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwaid.data.models.User
import com.siagajiwa.siagajiwaid.data.models.StressData
import com.siagajiwa.siagajiwaid.data.models.QuizData
import com.siagajiwa.siagajiwaid.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class UserViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            _uiState.value = _uiState.value.copy(isLoggedIn = true)
            loadUserProfile(currentUser.id)
        }
    }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.signInWithEmail(email, password)
                .onSuccess { userInfo ->
                    _uiState.value = _uiState.value.copy(isLoggedIn = true)
                    loadUserProfile(userInfo.id)
                    onSuccess()
                }
                .onFailure { exception ->
                    val errorMessage = exception.message ?: "Login failed"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                    onError(errorMessage)
                }
        }
    }

    fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.signUpWithEmail(email, password, fullName)
                .onSuccess { userInfo ->
                    _uiState.value = _uiState.value.copy(isLoggedIn = true)
                    loadUserProfile(userInfo.id)
                    onSuccess()
                }
                .onFailure { exception ->
                    val errorMessage = exception.message ?: "Sign up failed"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                    onError(errorMessage)
                }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.signOut()
                .onSuccess {
                    _uiState.value = UserUiState() // Reset to initial state
                    onComplete()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Sign out failed"
                    )
                }
        }
    }

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getUserProfile(userId)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load user profile"
                    )
                }
        }
    }

    fun saveStressTest(stressData: StressData, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.saveStressTest(stressData)
                .onSuccess {
                    // Reload user profile to get updated stress data
                    _uiState.value.user?.let { loadUserProfile(it.id) }
                    onComplete(true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to save stress test"
                    )
                    onComplete(false)
                }
        }
    }

    fun saveQuizResult(quizData: QuizData, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.saveQuizResult(quizData)
                .onSuccess {
                    // Reload user profile to get updated quiz data
                    _uiState.value.user?.let { loadUserProfile(it.id) }
                    onComplete(true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to save quiz result"
                    )
                    onComplete(false)
                }
        }
    }

    fun updateUserProfile(user: User, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.updateUserProfile(user)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(user = user)
                    onComplete(true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to update profile"
                    )
                    onComplete(false)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
