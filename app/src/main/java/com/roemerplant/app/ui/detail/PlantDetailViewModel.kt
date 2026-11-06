package com.roemerplant.app.ui.detail

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roemerplant.app.data.PlantRepository
import com.roemerplant.app.data.ai.PlantAiRepository
import com.roemerplant.app.data.model.Plant
import com.roemerplant.app.data.settings.SettingsRepository
import com.roemerplant.app.util.ImageHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlantDetailUiState(
    val plant: Plant? = null,
    val isEditing: Boolean = false,
    val isAnalyzing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    private val repository: PlantRepository,
    private val aiRepository: PlantAiRepository,
    private val imageHandler: ImageHandler,
    private val settings: SettingsRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val plantId: Long = savedStateHandle.get<Long>("plantId") ?: 0L

    private val _state = MutableStateFlow(PlantDetailUiState())
    val state: StateFlow<PlantDetailUiState> = _state

    val apiKeyConfigured: StateFlow<Boolean> = settings.mistralApiKey
        .map { it.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    init {
        viewModelScope.launch {
            repository.observeById(plantId).collect { plant ->
                _state.value = _state.value.copy(plant = plant)
            }
        }
    }

    fun updatePlant(transform: (Plant) -> Plant) {
        val current = _state.value.plant ?: return
        viewModelScope.launch { repository.save(transform(current)) }
    }

    fun setEditing(editing: Boolean) {
        _state.value = _state.value.copy(isEditing = editing)
    }

    fun analyzeHealth() {
        val plant = _state.value.plant ?: return
        val photoUri = plant.photoUri ?: return
        _state.value = _state.value.copy(isAnalyzing = true, error = null)
        viewModelScope.launch {
            val base64 = imageHandler.toBase64Jpeg(Uri.parse(photoUri))
            if (base64 == null) {
                _state.value = _state.value.copy(isAnalyzing = false, error = "Foto konnte nicht gelesen werden.")
                return@launch
            }
            aiRepository.analyzePlant(base64, "Pflanze laut Datenbank: ${plant.species.ifBlank { plant.name }}")
                .onSuccess { analysis ->
                    updatePlant { current -> repository.applyAnalysis(current, analysis) }
                    _state.value = _state.value.copy(isAnalyzing = false)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(isAnalyzing = false, error = error.message)
                }
        }
    }
}
