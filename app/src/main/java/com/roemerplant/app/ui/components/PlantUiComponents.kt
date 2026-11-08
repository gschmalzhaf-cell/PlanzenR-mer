package com.roemerplant.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            DividerDots()
            content()
        }
    }
}

@Composable
fun EditableField(
    label: String,
    value: String,
    minLines: Int = 1,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        minLines = minLines
    )
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    if (status.isBlank()) return
    val lower = status.lowercase()
    val (container, content, icon) = when {
        listOf("krank", "schädlingsbefall", "schädlings", "pilz").any { lower.contains(it) } ->
            Triple(
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer,
                Icons.Filled.BugReport
            )
        lower.contains("gestresst") || lower.contains("stress") ->
            Triple(
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.tertiary,
                Icons.Filled.Eco
            )
        else -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            Icons.Filled.Eco
        )
    }
    Surface(
        color = container,
        contentColor = content,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
            Text(status, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun DividerDots(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        listOf(Color(0xFF7A9B6D), Color(0xFF5B7552), Color(0xFF9BB391)).forEach { color ->
            Surface(
                color = color,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(5.dp)
            ) {}
        }
    }
}
