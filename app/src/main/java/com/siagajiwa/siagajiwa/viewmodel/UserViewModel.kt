package com.siagajiwa.siagajiwa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwa.data.models.User
import com.siagajiwa.siagajiwa.data.models.StressResult
import com.siagajiwa.siagajiwa.data.models.QuizData
import com.siagajiwa.siagajiwa.data.repository.UserRepository
import com.siagajiwa.siagajiwa.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class UserUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val latestStressResult: StressResult? = null,
    val latestQuizResult: QuizData? = null
)

class UserViewModel : ViewModel() {
    private val repository = UserRepository()
    private val quizRepository = QuizRepository()

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        println("🔍 [UserViewModel] Checking auth status on init...")
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            println("✅ [UserViewModel] Found existing session!")
            println("  User ID: ${currentUser.id}")
            println("  Email: ${currentUser.email}")
            println("  Metadata: ${currentUser.userMetadata}")

            // Create temporary user from auth info
            val tempUser = User(
                id = currentUser.id,
                email = currentUser.email ?: "",
                fullName = currentUser.userMetadata?.get("full_name")?.toString() ?: ""
            )

            _uiState.value = _uiState.value.copy(
                isLoggedIn = true,
                user = tempUser
            )

            // Load full user profile from database
            viewModelScope.launch {
                println("👤 [UserViewModel] Loading user profile from database...")
                repository.getUserProfile(currentUser.id)
                    .onSuccess { profileUser ->
                        println("✅ [UserViewModel] User profile loaded: ${profileUser.fullName}")
                        _uiState.value = _uiState.value.copy(user = profileUser)
                    }
                    .onFailure { profileError ->
                        println("⚠️ [UserViewModel] Could not load profile: ${profileError.message}")
                        println("🔧 [UserViewModel] Attempting to create missing profile...")

                        // Try to create the profile if it doesn't exist
                        val fullName = currentUser.userMetadata?.get("full_name")?.toString()
                            ?: currentUser.email?.substringBefore("@")
                            ?: "User"

                        repository.createUserProfile(
                            userId = currentUser.id,
                            email = currentUser.email ?: "",
                            fullName = fullName
                        ).onSuccess {
                            println("✅ [UserViewModel] Missing profile created successfully!")
                            // Reload the profile
                            repository.getUserProfile(currentUser.id)
                                .onSuccess { newProfileUser ->
                                    println("✅ [UserViewModel] Profile reloaded: ${newProfileUser.fullName}")
                                    _uiState.value = _uiState.value.copy(user = newProfileUser)
                                }
                        }.onFailure { createError ->
                            println("❌ [UserViewModel] Failed to create profile: ${createError.message}")
                            // Continue with temp user from metadata
                        }
                    }
            }

            // Load stress and quiz results
            loadLatestStressResult(currentUser.id)
            loadLatestQuizResult(currentUser.id)
        } else {
            println("⚠️ [UserViewModel] No existing session found - user needs to login")
        }
    }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            println("🔐 [UserViewModel] Starting sign in for: $email")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.signInWithEmail(email, password)
                .onSuccess { userInfo ->
                    println("✅ [UserViewModel] Sign in successful for user: ${userInfo.id}")

                    // Create temporary user from auth info
                    val tempUser = User(
                        id = userInfo.id,
                        email = userInfo.email ?: "",
                        fullName = userInfo.userMetadata?.get("full_name")?.toString() ?: ""
                    )

                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        isLoading = false,
                        user = tempUser
                    )

                    // Load full user profile from database (this will update the full name)
                    println("👤 [UserViewModel] Loading user profile from database...")
                    repository.getUserProfile(userInfo.id)
                        .onSuccess { profileUser ->
                            println("✅ [UserViewModel] User profile loaded: ${profileUser.fullName}")
                            _uiState.value = _uiState.value.copy(user = profileUser)
                        }
                        .onFailure { profileError ->
                            println("⚠️ [UserViewModel] Could not load profile: ${profileError.message}")
                            println("🔧 [UserViewModel] Attempting to create missing profile...")

                            // Try to create the profile if it doesn't exist
                            val fullName = userInfo.userMetadata?.get("full_name")?.toString()
                                ?: userInfo.email?.substringBefore("@")
                                ?: "User"

                            repository.createUserProfile(
                                userId = userInfo.id,
                                email = userInfo.email ?: "",
                                fullName = fullName
                            ).onSuccess {
                                println("✅ [UserViewModel] Missing profile created successfully!")
                                // Reload the profile
                                repository.getUserProfile(userInfo.id)
                                    .onSuccess { newProfileUser ->
                                        println("✅ [UserViewModel] Profile reloaded: ${newProfileUser.fullName}")
                                        _uiState.value = _uiState.value.copy(user = newProfileUser)
                                    }
                            }.onFailure { createError ->
                                println("❌ [UserViewModel] Failed to create profile: ${createError.message}")
                                // Continue with temp user from metadata
                            }
                        }

                    // Load stress and quiz results
                    loadLatestStressResult(userInfo.id)
                    loadLatestQuizResult(userInfo.id)

                    onSuccess()
                }
                .onFailure { exception ->
                    val errorMessage = exception.message ?: "Login failed"
                    println("❌ [UserViewModel] Sign in failed: $errorMessage")
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
                    println("✅ [UserViewModel] Sign up successful for user: ${userInfo.id}")

                    // Create temporary user from auth info
                    val tempUser = User(
                        id = userInfo.id,
                        email = userInfo.email ?: "",
                        fullName = fullName
                    )

                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        isLoading = false,
                        user = tempUser
                    )

                    // Load full user profile from database (this will update with any additional data)
                    println("👤 [UserViewModel] Loading user profile from database...")
                    repository.getUserProfile(userInfo.id)
                        .onSuccess { profileUser ->
                            println("✅ [UserViewModel] User profile loaded: ${profileUser.fullName}")
                            _uiState.value = _uiState.value.copy(user = profileUser)
                        }
                        .onFailure { profileError ->
                            println("⚠️ [UserViewModel] Could not load profile: ${profileError.message}")
                            // Continue with temp user
                        }

                    // Load stress and quiz results
                    loadLatestStressResult(userInfo.id)
                    loadLatestQuizResult(userInfo.id)

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
            println("👤 [UserViewModel] Loading user profile for: $userId")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getUserProfile(userId)
                .onSuccess { user ->
                    println("✅ [UserViewModel] User profile loaded: ${user.email}")
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        isLoading = false
                    )
                    // Also load the latest stress result and quiz result
                    loadLatestStressResult(userId)
                    loadLatestQuizResult(userId)
                }
                .onFailure { exception ->
                    println("❌ [UserViewModel] Failed to load user profile: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load user profile"
                    )
                }
        }
    }

    /**
     * Load the latest stress result from stress_result table
     * This is automatically called after loading user profile
     */
    fun loadLatestStressResult(userId: String) {
        viewModelScope.launch {
            println("🔍 [UserViewModel] Loading latest stress result for user: $userId")
            quizRepository.getLatestStressResult(userId)
                .onSuccess { stressResult ->
                    println("✅ [UserViewModel] Stress result loaded successfully: ${stressResult?.stressLevel}")
                    _uiState.value = _uiState.value.copy(
                        latestStressResult = stressResult
                    )
                }
                .onFailure { exception ->
                    // Don't show error for missing stress result - it's optional
                    // User may not have taken the test yet
                    println("⚠️ [UserViewModel] Failed to load stress result: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        latestStressResult = null
                    )
                }
        }
    }

    /**
     * Load the latest quiz result from quiz_results table
     * This is automatically called after loading user profile
     */
    fun loadLatestQuizResult(userId: String) {
        viewModelScope.launch {
            println("🔍 [UserViewModel] Loading latest quiz result for user: $userId")
            quizRepository.getLatestQuizResult(userId)
                .onSuccess { quizResult ->
                    println("✅ [UserViewModel] Quiz result loaded successfully: ${quizResult?.percentage}%")
                    _uiState.value = _uiState.value.copy(
                        latestQuizResult = quizResult
                    )
                }
                .onFailure { exception ->
                    // Don't show error for missing quiz result - it's optional
                    // User may not have taken the test yet
                    println("⚠️ [UserViewModel] Failed to load quiz result: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        latestQuizResult = null
                    )
                }
        }
    }

    /**
     * Refresh stress data for the current user
     * Call this after submitting a new stress test
     */
    fun refreshStressData() {
        _uiState.value.user?.let { user ->
            loadLatestStressResult(user.id)
        }
    }

    /**
     * Refresh quiz data for the current user
     * Call this after submitting a new quiz
     */
    fun refreshQuizData() {
        _uiState.value.user?.let { user ->
            loadLatestQuizResult(user.id)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
