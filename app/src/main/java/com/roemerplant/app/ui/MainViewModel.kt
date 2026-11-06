package com.roemerplant.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roemerplant.app.data.settings.SettingsRepository
import com.roemerplant.app.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settings: SettingsRepository
) : ViewModel() {

    val theme: StateFlow<AppTheme> = settings.appTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppTheme.FOREST)
}
