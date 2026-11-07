package com.roemerplant.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun RoemerPlantTheme(
    theme: AppTheme = AppTheme.HERBARIUM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorSchemeFor(theme, darkTheme),
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
