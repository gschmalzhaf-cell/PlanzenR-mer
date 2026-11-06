package com.roemerplant.app.ui.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.roemerplant.app.ui.components.EditableField
import com.roemerplant.app.ui.components.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    onSaved: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: AddPlantViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val apiKeyConfigured by viewModel.apiKeyConfigured.collectAsState()

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(viewModel::onPhotoPicked) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pflanze erfassen") },
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
            OutlinedButton(
                onClick = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                Text("Foto auswählen", modifier = Modifier.padding(start = 8.dp))
            }

            state.photoUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Pflanzenfoto",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(12.dp))
                )
                if (apiKeyConfigured) {
                    Button(
                        onClick = viewModel::analyzePhoto,
                        enabled = !state.isAnalyzing,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("KI analysiert Foto …")
                        } else {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                            Text("Mit Mistral-KI analysieren", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                } else {
                    Text(
                        "Für die KI-Analyse bitte zuerst den Mistral-API-Key in den Einstellungen hinterlegen.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            SectionCard("Allgemein") {
                EditableField("Name", state.name) { v -> viewModel.update { it.copy(name = v) } }
                EditableField("Art (z. B. Monstera deliciosa)", state.species) { v ->
                    viewModel.update { it.copy(species = v) }
                }
                EditableField(
                    "Beschreibung", state.description,
                    minLines = 3
                ) { v -> viewModel.update { it.copy(description = v) } }
            }

            SectionCard("Pflege") {
                EditableField("Licht", state.careLight) { v -> viewModel.update { it.copy(careLight = v) } }
                EditableField("Gießen", state.careWater) { v -> viewModel.update { it.copy(careWater = v) } }
                EditableField("Luftfeuchtigkeit", state.careHumidity) { v -> viewModel.update { it.copy(careHumidity = v) } }
                EditableField("Temperatur", state.careTemperature) { v -> viewModel.update { it.copy(careTemperature = v) } }
                EditableField("Düngen", state.careFertilizer) { v -> viewModel.update { it.copy(careFertilizer = v) } }
                EditableField("Substrat", state.careSoil) { v -> viewModel.update { it.copy(careSoil = v) } }
            }

            SectionCard("Gesundheit") {
                EditableField("Status", state.healthStatus) { v -> viewModel.update { it.copy(healthStatus = v) } }
                EditableField("Analyse", state.healthAnalysis, minLines = 3) { v ->
                    viewModel.update { it.copy(healthAnalysis = v) }
                }
                EditableField(
                    "Anweisungen (eine pro Zeile)", state.healthInstructions,
                    minLines = 3
                ) { v -> viewModel.update { it.copy(healthInstructions = v) } }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { viewModel.save(onSaved) },
                    enabled = !state.isSaving
                ) { Text("Speichern") }
            }
        }
    }
}
