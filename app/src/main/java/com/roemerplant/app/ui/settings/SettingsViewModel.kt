package com.roemerplant.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roemerplant.app.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val apiKeyInput: String = "",
    val savedKey: String = "",
    val isSaving: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

    init {
        viewModelScope.launch {
            val key = settings.mistralApiKey.first()
            _state.value = _state.value.copy(apiKeyInput = key, savedKey = key)
        }
    }

    fun onApiKeyChange(value: String) {
        _state.value = _state.value.copy(apiKeyInput = value)
    }

    fun saveApiKey() {
        val key = _state.value.apiKeyInput.trim()
        _state.value = _state.value.copy(isSaving = true)
        viewModelScope.launch {
            settings.setMistralApiKey(key)
            _state.value = _state.value.copy(savedKey = key, isSaving = false)
        }
    }
}
