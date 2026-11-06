package com.roemerplant.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.roemerplant.app.ui.components.SectionCard
import com.roemerplant.app.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard("Design") {
                Text(
                    "Wähle ein Farbschema – wird sofort angewendet und gespeichert.",
                    style = MaterialTheme.typography.bodySmall
                )
                AppTheme.entries.forEach { theme ->
                    RowSelect(theme.label, state.theme == theme) {
                        viewModel.onThemeChange(theme)
                    }
                }
            }

            SectionCard("Mistral-KI") {
                OutlinedTextField(
                    value = state.apiKeyInput,
                    onValueChange = viewModel::onApiKeyChange,
                    label = { Text("Mistral API-Key") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Der Key wird nur lokal auf dem Gerät gespeichert. Er ist unter https://console.mistral.ai/api-keys erhältlich.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            SectionCard("KI-Modell") {
                OutlinedTextField(
                    value = state.modelInput,
                    onValueChange = viewModel::onModelChange,
                    label = { Text("Modell") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = viewModel::loadModels,
                    enabled = !state.isLoadingModels,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoadingModels) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    Text("Verfügbare Modelle laden")
                }
                state.availableModels.forEach { model ->
                    TextButton(
                        onClick = { viewModel.onModelChange(model) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(model) }
                }
                state.message?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }

            Button(
                onClick = viewModel::save,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Speichern") }
        }
    }
}

@Composable
private fun RowSelect(label: String, selected: Boolean, onClick: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        TextButton(onClick = onClick) { Text(label) }
    }
}
