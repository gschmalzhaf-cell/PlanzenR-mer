package com.roemerplant.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roemerplant.app.data.ai.PlantAiRepository
import com.roemerplant.app.data.settings.SettingsRepository
import com.roemerplant.app.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val apiKeyInput: String = "",
    val savedKey: String = "",
    val modelInput: String = "",
    val theme: AppTheme = AppTheme.HERBARIUM,
    val availableModels: List<String> = emptyList(),
    val isLoadingModels: Boolean = false,
    val isSaving: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val aiRepository: PlantAiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

    init {
        viewModelScope.launch {
            val key = settings.mistralApiKey.first()
            val model = settings.mistralModel.first()
            val theme = settings.appTheme.first()
            _state.value = _state.value.copy(
                apiKeyInput = key,
                savedKey = key,
                modelInput = model,
                theme = theme
            )
        }
    }

    fun onApiKeyChange(value: String) {
        _state.value = _state.value.copy(apiKeyInput = value)
    }

    fun onModelChange(value: String) {
        _state.value = _state.value.copy(modelInput = value)
    }

    fun onThemeChange(theme: AppTheme) {
        _state.value = _state.value.copy(theme = theme)
        viewModelScope.launch { settings.setAppTheme(theme) }
    }

    fun loadModels() {
        _state.value = _state.value.copy(isLoadingModels = true, message = null)
        viewModelScope.launch {
            aiRepository.loadModels()
                .onSuccess { models ->
                    _state.value = _state.value.copy(availableModels = models, isLoadingModels = false)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoadingModels = false,
                        message = "Modelle konnten nicht geladen werden: ${error.message}"
                    )
                }
        }
    }

    fun save() {
        val key = _state.value.apiKeyInput.trim()
        val model = _state.value.modelInput.trim()
        _state.value = _state.value.copy(isSaving = true)
        viewModelScope.launch {
            settings.setMistralApiKey(key)
            settings.setMistralModel(model)
            _state.value = _state.value.copy(
                savedKey = key,
                isSaving = false,
                message = "Gespeichert."
            )
        }
    }
}
