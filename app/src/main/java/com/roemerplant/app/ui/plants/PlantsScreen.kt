package com.roemerplant.app.ui.plants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.roemerplant.app.data.model.Plant
import com.roemerplant.app.ui.components.LeafPattern
import com.roemerplant.app.ui.components.StatusBadge

@Composable
fun PlantsScreen(
    onAddPlant: () -> Unit,
    onOpenPlant: (Long) -> Unit,
    viewModel: PlantsViewModel = hiltViewModel()
) {
    val plants by viewModel.plants.collectAsState()

    if (plants.isEmpty()) {
        val pulse = rememberInfiniteTransition(label = "sprout").animateFloat(
            initialValue = 1f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
            label = "sproutScale"
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Eco,
                    contentDescription = null,
                    modifier = Modifier
                        .size(88.dp)
                        .scale(pulse.value),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Noch keine Pflanzen erfasst.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    "Hier könnte deine erste Pflanze wachsen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            items(plants, key = { it.id }) { plant ->
                PlantCard(
                    plant = plant,
                    onClick = { onOpenPlant(plant.id) },
                    onDelete = { viewModel.delete(plant) }
                )
            }
        }
    }
}

@Composable
private fun PlantCard(plant: Plant, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (plant.photoUri != null) {
                AsyncImage(
                    model = plant.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        Icons.Filled.Eco,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(plant.name.ifBlank { "Ohne Namen" }, style = MaterialTheme.typography.titleMedium)
                if (plant.species.isNotBlank()) {
                    Text(plant.species, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(plant.healthStatus)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Löschen")
                }
            }
        }
    }
}
