package com.roemerplant.app.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val apiKeyKey = stringPreferencesKey("mistral_api_key")
    private val modelKey = stringPreferencesKey("mistral_model")

    val mistralApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[apiKeyKey].orEmpty()
    }

    val mistralModel: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[modelKey] ?: DEFAULT_MODEL
    }

    suspend fun setMistralApiKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs[apiKeyKey] = key.trim()
        }
    }

    suspend fun setMistralModel(model: String) {
        context.dataStore.edit { prefs ->
            prefs[modelKey] = model.trim()
        }
    }

    companion object {
        const val DEFAULT_MODEL = "mistral-medium-3-5"
    }
}
