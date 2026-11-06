package com.roemerplant.app.ui.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roemerplant.app.data.PlantRepository
import com.roemerplant.app.data.ai.PlantAiRepository
import com.roemerplant.app.data.settings.SettingsRepository
import com.roemerplant.app.data.model.Plant
import com.roemerplant.app.util.ImageHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddPlantUiState(
    val name: String = "",
    val species: String = "",
    val family: String = "",
    val description: String = "",
    val careLight: String = "",
    val careWater: String = "",
    val careHumidity: String = "",
    val careTemperature: String = "",
    val careFertilizer: String = "",
    val careSoil: String = "",
    val healthStatus: String = "",
    val healthAnalysis: String = "",
    val healthInstructions: String = "",
    val photoUri: String? = null,
    val isAnalyzing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val repository: PlantRepository,
    private val aiRepository: PlantAiRepository,
    private val imageHandler: ImageHandler,
    settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddPlantUiState())
    val state: StateFlow<AddPlantUiState> = _state

    val apiKeyConfigured: StateFlow<Boolean> = settings.mistralApiKey
        .map { it.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun update(transform: (AddPlantUiState) -> AddPlantUiState) {
        _state.value = transform(_state.value)
    }

    fun onPhotoPicked(uri: Uri) {
        val copied = imageHandler.copyToInternalStorage(uri)
        _state.value = _state.value.copy(photoUri = copied ?: uri.toString())
    }

    fun createCameraCaptureUri(): Uri = imageHandler.createCameraCaptureUri()

    fun onPhotoTaken(uri: Uri, success: Boolean) {
        if (success) onPhotoPicked(uri)
    }

    fun analyzePhoto() {
        val photoUri = _state.value.photoUri ?: return
        _state.value = _state.value.copy(isAnalyzing = true, error = null)
        viewModelScope.launch {
            val base64 = imageHandler.toBase64Jpeg(Uri.parse(photoUri))
            if (base64 == null) {
                _state.value = _state.value.copy(isAnalyzing = false, error = "Foto konnte nicht gelesen werden.")
                return@launch
            }
            aiRepository.analyzePlant(base64)
                .onSuccess { analysis ->
                    val current = _state.value
                    _state.value = current.copy(
                        name = current.name.ifBlank { analysis.commonName.ifBlank { analysis.species } },
                        species = analysis.species,
                        family = analysis.family,
                        description = analysis.description,
                        careLight = analysis.care.light,
                        careWater = analysis.care.water,
                        careHumidity = analysis.care.humidity,
                        careTemperature = analysis.care.temperature,
                        careFertilizer = analysis.care.fertilizer,
                        careSoil = analysis.care.soil,
                        healthStatus = analysis.health.status,
                        healthAnalysis = analysis.health.analysis,
                        healthInstructions = analysis.health.instructions.joinToString("\n"),
                        isAnalyzing = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(isAnalyzing = false, error = error.message)
                }
        }
    }

    fun save(onSaved: (Long) -> Unit) {
        val s = _state.value
        if (s.name.isBlank() && s.species.isBlank()) {
            _state.value = s.copy(error = "Bitte einen Namen oder eine Art angeben.")
            return
        }
        _state.value = s.copy(isSaving = true, error = null)
        viewModelScope.launch {
            val id = repository.save(
                Plant(
                    name = s.name.ifBlank { s.species },
                    species = s.species,
                    family = s.family,
                    photoUri = s.photoUri,
                    description = s.description,
                    careLight = s.careLight,
                    careWater = s.careWater,
                    careHumidity = s.careHumidity,
                    careTemperature = s.careTemperature,
                    careFertilizer = s.careFertilizer,
                    careSoil = s.careSoil,
                    healthStatus = s.healthStatus,
                    healthAnalysis = s.healthAnalysis,
                    healthInstructions = s.healthInstructions,
                    lastAnalyzedAt = if (s.healthAnalysis.isNotBlank()) System.currentTimeMillis() else null
                )
            )
            onSaved(id)
        }
    }
}
