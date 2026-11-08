package com.roemerplant.app.ui.plants

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.roemerplant.app.data.model.Plant
import com.roemerplant.app.ui.components.LeafPattern
import com.roemerplant.app.ui.components.WashiTape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun PlantsScreen(
    onAddPlant: () -> Unit,
    onOpenPlant: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCollection: () -> Unit = {},
    collectionOnly: Boolean = false,
    viewModel: PlantsViewModel = hiltViewModel()
) {
    val plants by viewModel.plants.collectAsState()
    var query by remember { mutableStateOf("") }
    var sortByName by remember { mutableStateOf(true) }

    val filtered = remember(plants, query, sortByName) {
        val byQuery = if (query.isBlank()) plants else plants.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.species.contains(query, ignoreCase = true)
        }
        if (sortByName) byQuery.sortedBy { it.name.lowercase() } else byQuery.sortedByDescending { it.createdAt }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LeafPattern(modifier = Modifier.fillMaxSize())
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item(key = "topbar") {
                HomeTopBar(onOpenSettings = onOpenSettings)
            }
            if (collectionOnly) {
                item(key = "search") {
                    LeafSearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        onToggleSort = { sortByName = !sortByName }
                    )
                }
                if (plants.isEmpty()) {
                    item(key = "empty") { EmptyHerbarium(onAddPlant = onAddPlant) }
                } else {
                    item(key = "sammlung") {
                        HerbariumSection(
                            plants = filtered,
                            onOpenPlant = onOpenPlant,
                            onAddPlant = onAddPlant
                        )
                    }
                }
            } else {
            item(key = "greeting") { GreetingBanner(plantCount = plants.size) }
            item(key = "search") {
                LeafSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onToggleSort = { sortByName = !sortByName }
                )
            }
            item(key = "quickactions") {
                QuickActionRow(
                    onCamera = onAddPlant,
                    onIdentify = onAddPlant
                )
            }
            item(key = "wave1") { WavyDivider(color = MaterialTheme.colorScheme.surfaceContainerLow) }

            if (plants.isEmpty()) {
                item(key = "empty") { EmptyHerbarium(onAddPlant = onAddPlant) }
            } else {
                item(key = "carousel") {
                    PlantCarouselSection(
                        plants = filtered,
                        onOpenPlant = onOpenPlant,
                        onOpenCollection = onOpenCollection
                    )
                }
                item(key = "caretip") { CareTipCard() }
                item(key = "sammlung") {
                    HerbariumSection(
                        plants = filtered,
                        onOpenPlant = onOpenPlant,
                        onAddPlant = onAddPlant
                    )
                }
            }
            }
        }
    }
}

@Composable
private fun HomeTopBar(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = CircleShape,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Filled.Eco,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(9.dp)
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
            ) {
                Icon(
                    Icons.Filled.Eco,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                Icons.Filled.LocalFlorist,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "PflanzenR",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Box {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Benachrichtigungen",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 12.dp)
                    .size(10.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}

@Composable
private fun GreetingBanner(plantCount: Int) {
    val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val (greeting, mood) = when {
        hour < 5 -> "Gute Nacht" to "Selbst Moonflowers schlafen jetzt."
        hour < 11 -> "Guten Morgen" to "Heute ist ein herrlicher Tag zum Blühen."
        hour < 18 -> "Schönen Tag" to "Die Sonne küsst deine Blätter."
        else -> "Guten Abend" to "Zeit, die Blätter zur Ruhe zu betten."
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
                Icons.Filled.Spa,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(14.dp)
            )
            Text(
                "Herbar-Tagebuch & Begleiter",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            "$greeting, Pflanzenfreund \uD83C\uDF3F",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            "$mood · $plantCount Exemplare in deinem Herbarium",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun LeafSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onToggleSort: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp, topEnd = 12.dp, bottomStart = 12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                "Finde Pflanzen, Herbarium-Karten...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }
        Surface(
            onClick = onToggleSort,
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 8.dp, bottomStart = 8.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Tune,
                    contentDescription = "Sortierung wechseln",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun QuickActionRow(
    onCamera: () -> Unit,
    onIdentify: () -> Unit
) {
    val actions = listOf(
        Triple(Icons.Filled.PhotoCamera, "Kamera", MaterialTheme.colorScheme.surfaceContainerHigh to MaterialTheme.colorScheme.primary),
        Triple(Icons.Filled.TravelExplore, "Bestimmen", MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f) to MaterialTheme.colorScheme.tertiary)
    )
    val handlers = listOf(onCamera, onIdentify)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEachIndexed { index, (icon, label, colors) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .padding(2.dp)
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "actionScale")
                Surface(
                    onClick = handlers[index],
                    color = colors.first,
                    shape = RoundedCornerShape(16.dp),
                    border = if (index == 2) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)) else null,
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .size(56.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = label, tint = colors.second, modifier = Modifier.size(24.dp))
                    }
                }
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun WavyDivider(color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
    ) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.5f)
            cubicTo(
                size.width * 0.25f, size.height,
                size.width * 0.375f, 0f,
                size.width * 0.625f, size.height * 0.5f
            )
            cubicTo(
                size.width * 0.875f, size.height,
                size.width, size.height * 0.4f,
                size.width, size.height * 0.4f
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, color)
    }
}

@Composable
private fun PlantCarouselSection(
    plants: List<Plant>,
    onOpenPlant: (Long) -> Unit,
    onOpenCollection: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        Icons.Filled.Eco,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Meine Pflanzen", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    "${plants.size} grüne Schätze in deinem Garten",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                onClick = onOpenCollection,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Alle ansehen (${plants.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(plants, key = { it.id }) { plant ->
                PlantCarouselCard(
                    plant = plant,
                    onClick = { onOpenPlant(plant.id) }
                )
            }
        }
    }
}

@Composable
private fun PlantCarouselCard(
    plant: Plant,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(24.dp)
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = cardShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
        shadowElevation = 6.dp,
        modifier = Modifier.width(256.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(176.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (plant.photoUri != null) {
                    AsyncImage(
                        model = plant.photoUri,
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Eco,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }
                }
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            Icons.Filled.Eco,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            plant.healthStatus.ifBlank { "Munter & vital" },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    plant.name.ifBlank { "Ohne Namen" },
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    plant.healthStatus.ifBlank { "Munter & vital" },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
            if (plant.species.isNotBlank()) {
                Text(
                    plant.species,
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CareTipCard() {
    Surface(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.surfaceContainerLow,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "HEUTIGER PFLEGETIPP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Text(
                                "Morgenlicht",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        "Blätter sanft abstauben",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        "Eine zarte Dusche oder feuchtes Tuch befreit Monstera & Calathea von Staub für optimale Photosynthese.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HerbariumSection(
    plants: List<Plant>,
    onOpenPlant: (Long) -> Unit,
    onAddPlant: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd. MMM", Locale.GERMANY) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    Icons.Filled.LocalFlorist,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(22.dp)
                )
                Text("Herbarium Sammlung", style = MaterialTheme.typography.titleMedium)
            }
            Text(
                "Gepresste Schätze & botanische Funde",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        plants.chunked(2).forEach { rowPlants ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                rowPlants.forEach { plant ->
                    SpecimenCard(
                        plant = plant,
                        dateLabel = dateFormat.format(Date(plant.createdAt)),
                        onClick = { onOpenPlant(plant.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowPlants.size == 1) {
                    AddSpecimenCard(onClick = onAddPlant, modifier = Modifier.weight(1f))
                }
            }
        }
        if (plants.size % 2 == 0 && plants.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AddSpecimenCard(onClick = onAddPlant, modifier = Modifier.weight(1f))
                Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SpecimenCard(
    plant: Plant,
    dateLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Surface(
            onClick = onClick,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            shadowElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(144.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    if (plant.photoUri != null) {
                        AsyncImage(
                            model = plant.photoUri,
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Filled.Eco,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                    ) {
                        Text(
                            "Nr. %03d".format(plant.id),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    plant.name.ifBlank { "Unbenannter Fund" },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    plant.species.ifBlank { "Unbekannte Art" },
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Daheim",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        dateLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
        WashiTape(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-8).dp)
                .width(48.dp)
                .height(16.dp)
        )
    }
}

@Composable
private fun AddSpecimenCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                "Fundstück pressen",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                "Scanne eine Pflanze für dein digitales Herbarium",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun EmptyHerbarium(onAddPlant: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "sprout").animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "sproutScale"
    )
    val sway = rememberInfiniteTransition(label = "sway").animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "swayAngle"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 48.dp)
    ) {
        Icon(
            Icons.Filled.Eco,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(88.dp)
                .graphicsLayer {
                    rotationZ = sway.value
                    scaleX = pulse.value
                    scaleY = pulse.value
                }
        )
        Text(
            "Noch keine Pflanzen erfasst.",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            "Dein Herbarium liegt noch brach –",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "pflanze deinen ersten grünen Schatz!",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontStyle = FontStyle.Italic
            ),
            color = MaterialTheme.colorScheme.primary
        )
        TextButton(onClick = onAddPlant) { Text("Erste Pflanze hinzufügen") }
    }
}
