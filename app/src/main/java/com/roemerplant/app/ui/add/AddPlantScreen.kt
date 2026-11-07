package com.roemerplant.app.ui.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.roemerplant.app.ui.components.EditableField
import com.roemerplant.app.ui.components.GrowingPlantLoader
import com.roemerplant.app.ui.components.SectionCard

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

    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> cameraUri?.let { uri -> viewModel.onPhotoTaken(uri, success) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                onClick = onBack,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Zurück",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Column {
                Text(
                    "Fundstück erfassen",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Foto machen oder manuell eintragen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ViewfinderBox(
                photoUri = state.photoUri,
                onTakePhoto = {
                    val uri = viewModel.createCameraCaptureUri()
                    cameraUri = uri
                    takePicture.launch(uri)
                },
                onPickPhoto = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (state.photoUri != null) {
                if (apiKeyConfigured) {
                    Button(
                        onClick = viewModel::analyzePhoto,
                        enabled = !state.isAnalyzing,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isAnalyzing) {
                            GrowingPlantLoader(
                                modifier = Modifier.padding(end = 8.dp),
                                stemColor = MaterialTheme.colorScheme.onPrimary,
                                leafColor = MaterialTheme.colorScheme.onPrimary,
                                potColor = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("KI analysiert Foto …")
                        } else {
                            Icon(Icons.Filled.TravelExplore, contentDescription = null)
                            Text("Pflanze bestimmen", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                } else {
                    Text(
                        "Für die KI-Bestimmung bitte zuerst den Mistral-API-Key im Profil hinterlegen.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
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

            Button(
                onClick = { viewModel.save(onSaved) },
                enabled = !state.isSaving,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.LocalFlorist, contentDescription = null)
                Text(
                    "Ins Herbarium pressen",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun ViewfinderBox(
    photoUri: String?,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(28.dp))
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Pflanzenfoto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    onClick = onTakePhoto,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(50),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Filled.PhotoCamera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Neu aufnehmen",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Eco,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                modifier = Modifier.size(72.dp)
                            )
                            Text(
                                "Pflanze ins Visier nehmen",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            Text(
                                "Foto aufnehmen oder aus der Galerie wählen",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onTakePhoto,
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                Text("Foto aufnehmen", modifier = Modifier.padding(start = 8.dp))
            }
            OutlinedButton(
                onClick = onPickPhoto,
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                Text("Aus Galerie", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
