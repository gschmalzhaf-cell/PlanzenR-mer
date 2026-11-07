package com.roemerplant.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Serif = FontFamily.Serif

val AppTypography = Typography(
    headlineLarge = Typography().headlineLarge.copy(fontFamily = Serif),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = Serif),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = Serif),
    titleLarge = Typography().titleLarge.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
    titleMedium = Typography().titleMedium.copy(fontFamily = Serif, fontWeight = FontWeight.SemiBold),
    titleSmall = Typography().titleSmall.copy(fontFamily = Serif, fontWeight = FontWeight.Medium),
    labelLarge = Typography().labelLarge.copy(letterSpacing = 0.5.sp),
    labelMedium = Typography().labelMedium.copy(letterSpacing = 0.5.sp)
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)
