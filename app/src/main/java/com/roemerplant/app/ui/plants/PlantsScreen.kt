package com.roemerplant.app.ui.plants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.widget.Toast
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.roemerplant.app.data.model.Plant
import com.roemerplant.app.ui.components.LeafPattern
import com.roemerplant.app.ui.components.RefStamp
import com.roemerplant.app.ui.components.WashiTape
import com.roemerplant.app.ui.components.StatusBadge

@Composable
fun PlantsScreen(
    onAddPlant: () -> Unit,
    onOpenPlant: (Long) -> Unit,
    viewModel: PlantsViewModel = hiltViewModel()
) {
    val plants by viewModel.plants.collectAsState()
    val dailyCare by viewModel.dailyCare.collectAsState()
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    if (plants.isEmpty()) {
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Eco,
                    contentDescription = null,
                    modifier = Modifier
                        .size(88.dp)
                        .graphicsLayer {
                            rotationZ = sway.value
                            scaleX = pulse.value
                            scaleY = pulse.value
                        },
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Noch keine Pflanzen erfasst.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    "Dein Herbarium liegt noch brach \u2013",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "pflanze deine erste grüne Schätzling!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = onAddPlant) { Text("Erste Pflanze hinzufügen") }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LeafPattern(modifier = Modifier.fillMaxSize())
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "greeting") {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                    val greeting = when {
                        hour < 5 -> "Gute Nacht"
                        hour < 11 -> "Guten Morgen"
                        hour < 18 -> "Schönen Tag"
                        else -> "Guten Abend"
                    }
                    Text(
                        "$greeting, Gärtner!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "${plants.size} Exemplare in deinem Herbarium" +
                            (if (dailyCare.isNotEmpty()) " \u00b7 ${dailyCare.size} warten auf Zuwendung" else " \u00b7 alles versorgt"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (dailyCare.isNotEmpty()) {
                item(key = "curator") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Tages-Kuratel", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Pflegeschritte, die deine Zuwendung verlangen",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        dailyCare.forEach { item ->
                            DailyCareCard(
                                item = item,
                                onWater = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.markWatered(item.plant)
                                    Toast.makeText(context, "${item.plant.name} gegossen", Toast.LENGTH_SHORT).show()
                                },
                                onFertilize = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.markFertilized(item.plant)
                                    Toast.makeText(context, "${item.plant.name} gedüngt", Toast.LENGTH_SHORT).show()
                                },
                                onOpen = { onOpenPlant(item.plant.id) }
                            )
                        }
                    }
                }
            }
            items(plants, key = { it.id }) { plant ->
                Box(modifier = Modifier.animateItem()) {
                PlantCard(
                    plant = plant,
                    onClick = { onOpenPlant(plant.id) },
                    onDelete = { viewModel.delete(plant) },
                    onWater = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.markWatered(plant)
                        Toast.makeText(context, "${plant.name} gegossen", Toast.LENGTH_SHORT).show()
                    },
                    onFertilize = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.markFertilized(plant)
                        Toast.makeText(context, "${plant.name} gedüngt", Toast.LENGTH_SHORT).show()
                    }
                )
                }
            }
        }
    }
}

@Composable
private fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onWater: () -> Unit,
    onFertilize: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        label = "cardScale"
    )
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = cardScale
                    scaleY = cardScale
                }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    if (plant.photoUri != null) {
                        AsyncImage(
                            model = plant.photoUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Filled.Eco,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxSize(),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    RefStamp(
                        ref = "REF #%03d".format(plant.id),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    )
                }
                if (plant.species.isNotBlank()) {
                    Text(
                        plant.species,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            fontFamily = FontFamily.Serif
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                Text(
                    plant.name.ifBlank { "Ohne Namen" },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(plant.healthStatus, modifier = Modifier.weight(1f))
                    IconButton(onClick = onWater) {
                        Icon(
                            Icons.Filled.WaterDrop,
                            contentDescription = "Gegossen",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    IconButton(onClick = onFertilize) {
                        Icon(
                            Icons.Filled.Science,
                            contentDescription = "Gedüngt",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Löschen")
                    }
                }
            }
        }
        WashiTape(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(width = 72.dp, height = 18.dp)
        )
    }
}

@Composable
private fun DailyCareCard(
    item: DailyCareItem,
    onWater: () -> Unit,
    onFertilize: () -> Unit,
    onOpen: () -> Unit
) {
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (item.plant.photoUri != null) {
                AsyncImage(
                    model = item.plant.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.Eco,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                if (item.waterDue) {
                    Text(
                        if (item.overdueDays in 1..9999) "Wasser dringend · ${item.overdueDays} Tage überfällig" else "Gießen fällig",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                } else {
                    Text(
                        "Düngung fällig",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Text(item.plant.name, style = MaterialTheme.typography.titleMedium)
            }
            if (item.waterDue) {
                val urgentPulse = rememberInfiniteTransition(label = "urgent").animateFloat(
                    initialValue = 1f,
                    targetValue = 1.25f,
                    animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
                    label = "urgentScale"
                )
                IconButton(onClick = onWater) {
                    Icon(
                        Icons.Filled.WaterDrop,
                        contentDescription = "Gegossen",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.graphicsLayer {
                            scaleX = urgentPulse.value
                            scaleY = urgentPulse.value
                        }
                    )
                }
            }
            if (item.fertilizeDue) {
                IconButton(onClick = onFertilize) {
                    Icon(
                        Icons.Filled.Science,
                        contentDescription = "Gedüngt",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
