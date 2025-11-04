package com.siagajiwa.siagajiwa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siagajiwa.siagajiwa.data.models.MediaContent
import com.siagajiwa.siagajiwa.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Media Content
 */
sealed class MediaUiState {
    object Loading : MediaUiState()
    data class Success(val media: List<MediaContent>) : MediaUiState()
    data class Error(val message: String) : MediaUiState()
}

/**
 * ViewModel for managing media content from Supabase
 * Handles: Stress Management, Patient Care, and Schizophrenia Insight media
 */
class MediaViewModel : ViewModel() {
    private val repository = MediaRepository()

    // State flows for each media type
    private val _stressMediaState = MutableStateFlow<MediaUiState>(MediaUiState.Loading)
    val stressMediaState: StateFlow<MediaUiState> = _stressMediaState.asStateFlow()

    private val _patientCareMediaState = MutableStateFlow<MediaUiState>(MediaUiState.Loading)
    val patientCareMediaState: StateFlow<MediaUiState> = _patientCareMediaState.asStateFlow()

    private val _schizophreniaMediaState = MutableStateFlow<MediaUiState>(MediaUiState.Loading)
    val schizophreniaMediaState: StateFlow<MediaUiState> = _schizophreniaMediaState.asStateFlow()

    /**
     * Load stress management media from Supabase
     */
    fun loadStressMedia() {
        viewModelScope.launch {
            _stressMediaState.value = MediaUiState.Loading
            val result = repository.getStressMedia()

            _stressMediaState.value = result.fold(
                onSuccess = { media ->
                    if (media.isEmpty()) {
                        MediaUiState.Error("No stress management media found")
                    } else {
                        MediaUiState.Success(media)
                    }
                },
                onFailure = { exception ->
                    MediaUiState.Error(exception.message ?: "Failed to load stress media")
                }
            )
        }
    }

    /**
     * Load patient care media from Supabase
     */
    fun loadPatientCareMedia() {
        viewModelScope.launch {
            _patientCareMediaState.value = MediaUiState.Loading
            val result = repository.getPatientCareMedia()

            _patientCareMediaState.value = result.fold(
                onSuccess = { media ->
                    if (media.isEmpty()) {
                        MediaUiState.Error("No patient care media found")
                    } else {
                        MediaUiState.Success(media)
                    }
                },
                onFailure = { exception ->
                    MediaUiState.Error(exception.message ?: "Failed to load patient care media")
                }
            )
        }
    }

    /**
     * Load schizophrenia insight media from Supabase
     */
    fun loadSchizophreniaMedia() {
        viewModelScope.launch {
            _schizophreniaMediaState.value = MediaUiState.Loading
            val result = repository.getSchizophreniaMedia()

            _schizophreniaMediaState.value = result.fold(
                onSuccess = { media ->
                    if (media.isEmpty()) {
                        MediaUiState.Error("No schizophrenia media found")
                    } else {
                        MediaUiState.Success(media)
                    }
                },
                onFailure = { exception ->
                    MediaUiState.Error(exception.message ?: "Failed to load schizophrenia media")
                }
            )
        }
    }

    /**
     * Retry loading media based on the type
     */
    fun retryLoading(mediaType: MediaType) {
        when (mediaType) {
            MediaType.STRESS -> loadStressMedia()
            MediaType.PATIENT_CARE ->  loadPatientCareMedia()
            MediaType.SCHIZOPHRENIA -> loadSchizophreniaMedia()
        }
    }
}

/**
 * Enum to identify media type for retry operations
 */
enum class MediaType {
    STRESS,
    PATIENT_CARE,
    SCHIZOPHRENIA
}
