package com.roemerplant.app.ui.detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import android.net.Uri
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.roemerplant.app.ui.components.CareIcons
import com.roemerplant.app.ui.components.EditableField
import com.roemerplant.app.ui.components.InfoRow
import com.roemerplant.app.ui.components.SectionCard
import com.roemerplant.app.ui.components.StatusBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    plantId: Long,
    onBack: () -> Unit,
    viewModel: PlantDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val apiKeyConfigured by viewModel.apiKeyConfigured.collectAsState()
    val plant = state.plant

    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> cameraUri?.let { uri -> viewModel.onPhotoTaken(uri, success) } }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(viewModel::onPhotoPicked) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant?.name ?: "Pflanze") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.setEditing(!state.isEditing) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Bearbeiten")
                    }
                }
            )
        }
    ) { padding ->
        if (plant == null) {
            Text(
                "Pflanze nicht gefunden.",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            plant.photoUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Pflanzenfoto",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (state.isEditing) {
                SectionCard("Allgemein") {
                    EditableField("Name", plant.name) { v -> viewModel.updatePlant { it.copy(name = v, updatedAt = System.currentTimeMillis()) } }
                    EditableField("Art", plant.species) { v -> viewModel.updatePlant { it.copy(species = v, updatedAt = System.currentTimeMillis()) } }
                    EditableField("Beschreibung", plant.description, minLines = 3) { v -> viewModel.updatePlant { it.copy(description = v, updatedAt = System.currentTimeMillis()) } }
                }
                SectionCard("Pflege") {
                    EditableField("Licht", plant.careLight) { v -> viewModel.updatePlant { it.copy(careLight = v) } }
                    EditableField("Gießen", plant.careWater) { v -> viewModel.updatePlant { it.copy(careWater = v) } }
                    EditableField("Luftfeuchtigkeit", plant.careHumidity) { v -> viewModel.updatePlant { it.copy(careHumidity = v) } }
                    EditableField("Temperatur", plant.careTemperature) { v -> viewModel.updatePlant { it.copy(careTemperature = v) } }
                    EditableField("Düngen", plant.careFertilizer) { v -> viewModel.updatePlant { it.copy(careFertilizer = v) } }
                    EditableField("Substrat", plant.careSoil) { v -> viewModel.updatePlant { it.copy(careSoil = v) } }
                }
                SectionCard("Gesundheit") {
                    EditableField("Status", plant.healthStatus) { v -> viewModel.updatePlant { it.copy(healthStatus = v) } }
                    EditableField("Analyse", plant.healthAnalysis, minLines = 3) { v -> viewModel.updatePlant { it.copy(healthAnalysis = v) } }
                    EditableField("Anweisungen (eine pro Zeile)", plant.healthInstructions, minLines = 3) { v -> viewModel.updatePlant { it.copy(healthInstructions = v) } }
                }
            } else {
                SectionCard("Allgemein") {
                    InfoRow("Art", plant.species)
                    InfoRow("Beschreibung", plant.description)
                }
                SectionCard("Pflege") {
                    InfoRow("Licht", plant.careLight, CareIcons.light)
                    InfoRow("Gießen", plant.careWater, CareIcons.water)
                    InfoRow("Luftfeuchtigkeit", plant.careHumidity, CareIcons.humidity)
                    InfoRow("Temperatur", plant.careTemperature, CareIcons.temperature)
                    InfoRow("Düngen", plant.careFertilizer, CareIcons.fertilizer)
                    InfoRow("Substrat", plant.careSoil, CareIcons.soil)
                }
                SectionCard("Gesundheit") {
                    StatusBadge(plant.healthStatus)
                    InfoRow("Status", plant.healthStatus)
                    InfoRow("Analyse", plant.healthAnalysis)
                    if (plant.healthInstructions.isNotBlank()) {
                        Column {
                            Text("Anweisungen", style = MaterialTheme.typography.labelMedium)
                            plant.healthInstructions.lines()
                                .filter { it.isNotBlank() }
                                .forEach { instruction ->
                                    Text("• $instruction", style = MaterialTheme.typography.bodyMedium)
                                }
                        }
                    }
                    plant.lastAnalyzedAt?.let { timestamp ->
                        val formatted = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN)
                            .format(Date(timestamp))
                        InfoRow("Letzte Analyse", formatted)
                    }
                }
            }

            if (!state.isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val uri = viewModel.createCameraCaptureUri()
                            cameraUri = uri
                            takePicture.launch(uri)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                        Text("Neues Foto", modifier = Modifier.padding(start = 8.dp))
                    }
                    OutlinedButton(
                        onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                        Text("Auswählen", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            if (plant.photoUri != null && apiKeyConfigured && !state.isEditing) {
                Button(
                    onClick = viewModel::analyzeHealth,
                    enabled = !state.isAnalyzing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("KI analysiert …")
                    } else {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                        Text("Gesundheit mit Foto analysieren", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Zurück zur Übersicht")
            }
        }
    }
}
