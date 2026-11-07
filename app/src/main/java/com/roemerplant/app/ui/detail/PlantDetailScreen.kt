package com.roemerplant.app.ui.detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import android.net.Uri
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import com.roemerplant.app.ui.components.EditableField
import com.roemerplant.app.ui.components.GrowingPlantLoader
import com.roemerplant.app.ui.components.SectionCard
import com.roemerplant.app.ui.components.StatusBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    if (plant == null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Pflanze nicht gefunden.")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 112.dp)
        ) {
            DetailHeaderRow(
                onBack = onBack,
                isEditing = state.isEditing,
                onToggleEdit = { viewModel.setEditing(!state.isEditing) },
                onDelete = {
                    viewModel.delete()
                    onBack()
                }
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                HeroPhoto(
                    plant = plant,
                    onBack = onBack,
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

                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                if (state.isEditing) {
                    EditSections(plant, viewModel)
                } else {
                    TitleRow(plant)
                    if (plant.description.isNotBlank()) {
                        Text(
                            plant.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    CareIndicatorGrid(plant)
                    CareInstructionsCard(plant)
                    SteckbriefCard(plant)
                    if (plant.healthStatus.isNotBlank() || plant.healthAnalysis.isNotBlank()) {
                        HealthCard(plant)
                    }
                    JournalCard(plant)
                    ActionRow(
                        plant = plant,
                        apiKeyConfigured = apiKeyConfigured,
                        isAnalyzing = state.isAnalyzing,
                        onAnalyze = viewModel::analyzeHealth
                    )
                }
            }
        }

    }
}

@Composable
private fun DetailHeaderRow(
    onBack: () -> Unit,
    isEditing: Boolean,
    onToggleEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
            Text(
                "PflanzenR",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
                onClick = onToggleEdit,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = if (isEditing) "Fertig" else "Bearbeiten",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Surface(
                onClick = onDelete,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Löschen",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroPhoto(
    plant: com.roemerplant.app.data.model.Plant,
    onBack: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        if (plant.photoUri != null) {
            AsyncImage(
                model = plant.photoUri,
                contentDescription = "Pflanzenfoto",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Eco,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(96.dp)
                    )
                }
            }
        }
        Surface(
            onClick = onBack,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shape = CircleShape,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Surface(
            onClick = onTakePhoto,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shape = CircleShape,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp)) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = "Neues Foto",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shape = RoundedCornerShape(50),
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Filled.Eco,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    plant.family.ifBlank { "Zimmerpflanze" },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Surface(
            onClick = onPickPhoto,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shape = RoundedCornerShape(50),
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Filled.PhotoLibrary,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "Foto wechseln",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun TitleRow(plant: com.roemerplant.app.data.model.Plant) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            plant.name.ifBlank { "Ohne Namen" },
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        if (plant.species.isNotBlank()) {
            Text(
                plant.species,
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun CareIndicatorGrid(plant: com.roemerplant.app.data.model.Plant) {
    val tiles = listOf(
        CareTile(Icons.Filled.WaterDrop, "Wasser", plant.careWater.ifBlank { "Erde antrocknen lassen" }, "Mäßig", MaterialTheme.colorScheme.primary),
        CareTile(Icons.Filled.WbSunny, "Licht", plant.careLight.ifBlank { "Indirektes Sonnenlicht" }, "Hell", MaterialTheme.colorScheme.secondary),
        CareTile(Icons.Outlined.Air, "Luftfeuchte", plant.careHumidity.ifBlank { "Gerne besprühen" }, "Hoch", MaterialTheme.colorScheme.primary),
        CareTile(Icons.Filled.Thermostat, "Temperatur", plant.careTemperature.ifBlank { "18 – 25 °C" }, "Warm", MaterialTheme.colorScheme.tertiary)
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        tiles.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { tile ->
                    CareIndicatorTile(tile, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

private data class CareTile(
    val icon: ImageVector,
    val label: String,
    val hint: String,
    val level: String,
    val tint: androidx.compose.ui.graphics.Color
)

@Composable
private fun CareIndicatorTile(tile: CareTile, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(tile.icon, contentDescription = null, tint = tile.tint, modifier = Modifier.size(22.dp))
                Surface(color = tile.tint.copy(alpha = 0.12f), shape = RoundedCornerShape(50)) {
                    Text(
                        tile.level,
                        style = MaterialTheme.typography.labelSmall,
                        color = tile.tint,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Column {
                Text(tile.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    tile.hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    iconContainer: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            color = iconContainer,
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
        }
        Text(title, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun LeafBulletPoint(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(
            Icons.Filled.LocalFlorist,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(top = 2.dp).size(16.dp)
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HerbariumCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(18.dp)) {
            content()
        }
    }
}

@Composable
private fun CareInstructionsCard(plant: com.roemerplant.app.data.model.Plant) {
    HerbariumCard {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionHeader(
                icon = Icons.Filled.Spa,
                iconContainer = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                iconTint = MaterialTheme.colorScheme.primary,
                title = "Pflegehinweise"
            )
            val instructions = buildList {
                if (plant.careFertilizer.isNotBlank()) add("Düngung: ${plant.careFertilizer}")
                if (plant.careSoil.isNotBlank()) add("Substrat: ${plant.careSoil}")
                plant.healthInstructions.lines()
                    .filter { it.isNotBlank() }
                    .forEach { add(it) }
            }
            if (instructions.isEmpty()) {
                LeafBulletPoint("Noch keine Pflegehinweise erfasst – tippe auf den Stift, um welche zu notieren.")
            } else {
                instructions.forEach { LeafBulletPoint(it) }
            }
        }
    }
}

@Composable
private fun SteckbriefCard(plant: com.roemerplant.app.data.model.Plant) {
    HerbariumCard {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionHeader(
                icon = Icons.Filled.MenuBook,
                iconContainer = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                iconTint = MaterialTheme.colorScheme.secondary,
                title = "Steckbrief"
            )
            val dateFormat = remember { SimpleDateFormat("dd. MMM yyyy", Locale.GERMANY) }
            val tiles = listOf(
                "Familie" to plant.family.ifBlank { "Unbekannt" },
                "Art" to plant.species.ifBlank { "Unbestimmt" },
                "Erfasst am" to dateFormat.format(Date(plant.createdAt)),
                "Gießintervall" to "Alle ${plant.waterIntervalDays} Tage"
            )
            tiles.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { (label, value) ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HealthCard(plant: com.roemerplant.app.data.model.Plant) {
    HerbariumCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                icon = Icons.Filled.AutoAwesome,
                iconContainer = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                iconTint = MaterialTheme.colorScheme.tertiary,
                title = "Gesundheit"
            )
            StatusBadge(plant.healthStatus)
            if (plant.healthAnalysis.isNotBlank()) {
                Text(
                    plant.healthAnalysis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            plant.lastAnalyzedAt?.let { timestamp ->
                val formatted = remember(timestamp) {
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN).format(Date(timestamp))
                }
                Text(
                    "Letzte Analyse: $formatted",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun JournalCard(plant: com.roemerplant.app.data.model.Plant) {
    val dateFormat = remember { SimpleDateFormat("dd. MMM yyyy", Locale.GERMANY) }
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Icon(
                Icons.Filled.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(140.dp)
            )
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape,
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.EditNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text("Tagebuch-Notiz", style = MaterialTheme.typography.titleMedium)
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(50),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            dateFormat.format(Date(plant.updatedAt)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    plant.description.ifBlank { "Noch keine Notizen – halte hier deine Beobachtungen fest." },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Notiz bearbeiten über den Stift oben",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun ActionRow(
    plant: com.roemerplant.app.data.model.Plant,
    apiKeyConfigured: Boolean,
    isAnalyzing: Boolean,
    onAnalyze: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (plant.photoUri != null && apiKeyConfigured) {
            Button(
                onClick = onAnalyze,
                enabled = !isAnalyzing,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isAnalyzing) {
                    GrowingPlantLoader(
                        modifier = Modifier.padding(end = 8.dp),
                        stemColor = MaterialTheme.colorScheme.onPrimary,
                        leafColor = MaterialTheme.colorScheme.onPrimary,
                        potColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Text("KI analysiert …")
                } else {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                    Text("Gesundheit mit Foto analysieren", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun EditSections(
    plant: com.roemerplant.app.data.model.Plant,
    viewModel: PlantDetailViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionCard("Allgemein") {
            EditableField("Name", plant.name) { v -> viewModel.updatePlant { it.copy(name = v, updatedAt = System.currentTimeMillis()) } }
            EditableField("Art", plant.species) { v -> viewModel.updatePlant { it.copy(species = v, updatedAt = System.currentTimeMillis()) } }
            EditableField("Familie", plant.family) { v -> viewModel.updatePlant { it.copy(family = v) } }
            EditableField("Beschreibung", plant.description, minLines = 3) { v -> viewModel.updatePlant { it.copy(description = v, updatedAt = System.currentTimeMillis()) } }
        }
        SectionCard("Pflege") {
            EditableField("Licht", plant.careLight) { v -> viewModel.updatePlant { it.copy(careLight = v) } }
            EditableField("Gießen", plant.careWater) { v -> viewModel.updatePlant { it.copy(careWater = v) } }
            EditableField("Luftfeuchtigkeit", plant.careHumidity) { v -> viewModel.updatePlant { it.copy(careHumidity = v) } }
            EditableField("Temperatur", plant.careTemperature) { v -> viewModel.updatePlant { it.copy(careTemperature = v) } }
            EditableField("Düngen", plant.careFertilizer) { v -> viewModel.updatePlant { it.copy(careFertilizer = v) } }
            EditableField("Substrat", plant.careSoil) { v -> viewModel.updatePlant { it.copy(careSoil = v) } }
            EditableField("Gieß-Intervall (Tage)", plant.waterIntervalDays.toString()) { v ->
                viewModel.updatePlant { it.copy(waterIntervalDays = v.toIntOrNull() ?: it.waterIntervalDays) }
            }
            EditableField("Dünge-Intervall (Tage)", plant.fertilizeIntervalDays.toString()) { v ->
                viewModel.updatePlant { it.copy(fertilizeIntervalDays = v.toIntOrNull() ?: it.fertilizeIntervalDays) }
            }
        }
        SectionCard("Gesundheit") {
            EditableField("Status", plant.healthStatus) { v -> viewModel.updatePlant { it.copy(healthStatus = v) } }
            EditableField("Analyse", plant.healthAnalysis, minLines = 3) { v -> viewModel.updatePlant { it.copy(healthAnalysis = v) } }
            EditableField("Anweisungen (eine pro Zeile)", plant.healthInstructions, minLines = 3) { v -> viewModel.updatePlant { it.copy(healthInstructions = v) } }
        }
    }
}
