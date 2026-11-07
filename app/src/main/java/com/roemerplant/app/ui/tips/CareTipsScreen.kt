package com.roemerplant.app.ui.tips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.roemerplant.app.ui.components.LeafPattern

private data class CareTip(
    val icon: ImageVector,
    val title: String,
    val tag: String,
    val body: String
)

private val careTips = listOf(
    CareTip(
        icon = Icons.Filled.WaterDrop,
        title = "Gießen mit Fingerspitzengefühl",
        tag = "Wasser",
        body = "Prüfe die Erde zwei Fingerbreit tief: Ist sie trocken, reicht eine sanfte Dusche bis das Wasser unten abläuft. Staunässe mögen nur Sumpfpflanzen."
    ),
    CareTip(
        icon = Icons.Filled.WbSunny,
        title = "Licht nach Bedarf",
        tag = "Licht",
        body = "Blättrige Schönheiten wie Monstera lieben helles, indirektes Licht. Direkte Mittagssonne kann zarte Blätter verbrennen – ein paar Meter vom Fenster wirkt Wunder."
    ),
    CareTip(
        icon = Icons.Filled.Opacity,
        title = "Luftfeuchte für Tropen",
        tag = "Klima",
        body = "Calathea & Farne freuen sich über 60 % Luftfeuchte. Wasserzerstäuber, Kieselsteine im Untersetzer oder nette Pflanzennachbarn helfen."
    ),
    CareTip(
        icon = Icons.Filled.Science,
        title = "Düngen nur in der Wachstumszeit",
        tag = "Nährstoffe",
        body = "Von Frühjahr bis Sommer alle zwei Wochen halb konzentriert düngen. Im Winter ruhen die Wurzeln – dann bitte sparsam bleiben."
    ),
    CareTip(
        icon = Icons.Filled.Spa,
        title = "Blätter sanft abstauben",
        tag = "Pflege",
        body = "Ein feuchtes Tuch befreit große Blätter von Staub, damit die Photosynthese richtig arbeiten kann. Danach glänzen sie wie poliert."
    ),
    CareTip(
        icon = Icons.Filled.Lightbulb,
        title = "Umtopfen im Frühling",
        tag = "Wurzeln",
        body = "Schauen Wurzeln aus dem Topfboden, freut sich die Pflanze über ein Gefäß 2–3 cm größer mit frischer, luftiger Erde."
    )
)

@Composable
fun CareTipsScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        LeafPattern(modifier = Modifier.fillMaxSize())
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "header") {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Spa,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Wissen aus dem Gewächshaus",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Text(
                        "Pflegetipps",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        "Kleine Rituale für großes Gedeihen",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(careTips.size) { index ->
                val tip = careTips[index]
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    tip.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    tip.title,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    tip.tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                tip.body,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
            item(key = "footer") {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Filled.LocalFlorist,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Jede Pflanze erzählt ihre eigene Geschichte – beobachte sie mit Geduld.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
