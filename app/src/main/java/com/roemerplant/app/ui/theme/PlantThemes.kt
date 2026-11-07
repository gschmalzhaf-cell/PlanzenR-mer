package com.roemerplant.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppTheme(val label: String) {
    HERBARIUM("Herbarium", 0xFF7A9B6D),
    FOREST("Waldgrün", 0xFF2E7D32),
    BLOSSOM("Blütenrosa", 0xFF9C4351),
    TERRACOTTA("Terrakotta", 0xFF8F4C2F)
}

private val HerbariumLight = lightColorScheme(
    primary = Color(0xFF48663D),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF7A9B6D),
    onPrimaryContainer = Color(0xFF15310E),
    secondary = Color(0xFF8C4E30),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFAE89),
    onSecondaryContainer = Color(0xFF793F22),
    tertiary = Color(0xFF865046),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC18377),
    onTertiaryContainer = Color(0xFF481F17),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
    background = Color(0xFFFDF9F1),
    onBackground = Color(0xFF1C1C17),
    surface = Color(0xFFFDF9F1),
    onSurface = Color(0xFF1C1C17),
    surfaceVariant = Color(0xFFE6E2DA),
    onSurfaceVariant = Color(0xFF43483F),
    outline = Color(0xFF73796E),
    outlineVariant = Color(0xFFC3C8BC),
    inverseSurface = Color(0xFF31302B),
    inverseOnSurface = Color(0xFFF4F0E8),
    inversePrimary = Color(0xFFADD09E),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F3EB),
    surfaceContainer = Color(0xFFF1EDE6),
    surfaceContainerHigh = Color(0xFFECE8E0),
    surfaceContainerHighest = Color(0xFFE6E2DA)
)

private val HerbariumDark = darkColorScheme(
    primary = Color(0xFFA3C293),
    onPrimary = Color(0xFF12210D),
    primaryContainer = Color(0xFF3E5233),
    onPrimaryContainer = Color(0xFFC9EDB9),
    secondary = Color(0xFFE0A183),
    onSecondary = Color(0xFF3A1B0E),
    secondaryContainer = Color(0xFF6B3A22),
    onSecondaryContainer = Color(0xFFFFDBCC),
    tertiary = Color(0xFFE8A598),
    onTertiary = Color(0xFF351009),
    tertiaryContainer = Color(0xFF6A3930),
    onTertiaryContainer = Color(0xFFFFDAD3),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1C231A),
    onBackground = Color(0xFFECE8E0),
    surface = Color(0xFF20281E),
    onSurface = Color(0xFFECE8E0),
    surfaceVariant = Color(0xFF43483F),
    onSurfaceVariant = Color(0xFFC3C8BC),
    outline = Color(0xFF8D9389),
    outlineVariant = Color(0xFF43483F),
    inverseSurface = Color(0xFFE6E2DA),
    inverseOnSurface = Color(0xFF31302B),
    inversePrimary = Color(0xFF48663D),
    surfaceContainerLowest = Color(0xFF131811),
    surfaceContainerLow = Color(0xFF1A2118),
    surfaceContainer = Color(0xFF20281E),
    surfaceContainerHigh = Color(0xFF2A3324),
    surfaceContainerHighest = Color(0xFF353F2E)
)

private val ForestLight = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB9F0B9),
    onPrimaryContainer = Color(0xFF00210A),
    secondary = Color(0xFF52634F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD5E8CE),
    onSecondaryContainer = Color(0xFF101F10),
    tertiary = Color(0xFF38656A),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF7FBF2),
    onBackground = Color(0xFF181D17),
    surface = Color(0xFFF7FBF2),
    onSurface = Color(0xFF181D17),
    surfaceVariant = Color(0xFFDEE5D9),
    onSurfaceVariant = Color(0xFF424940),
    outline = Color(0xFF72796F)
)

private val ForestDark = darkColorScheme(
    primary = Color(0xFFB9F0B9),
    onPrimary = Color(0xFF00210A),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFB9F0B9),
    secondary = Color(0xFFD5E8CE),
    onSecondary = Color(0xFF101F10),
    secondaryContainer = Color(0xFF52634F),
    onSecondaryContainer = Color(0xFFD5E8CE),
    tertiary = Color(0xFF9CD1D6),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF181D17),
    onBackground = Color(0xFFE1E6DC),
    surface = Color(0xFF181D17),
    onSurface = Color(0xFFE1E6DC),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC2C9BD),
    outline = Color(0xFF8C9392)
)

private val BlossomLight = lightColorScheme(
    primary = Color(0xFF9C4351),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD9DE),
    onPrimaryContainer = Color(0xFF400014),
    secondary = Color(0xFF76565A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9DE),
    onSecondaryContainer = Color(0xFF2C1518),
    tertiary = Color(0xFF7B5735),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFF8F7),
    onBackground = Color(0xFF221919),
    surface = Color(0xFFFFF8F7),
    onSurface = Color(0xFF221919),
    surfaceVariant = Color(0xFFF3DDDE),
    onSurfaceVariant = Color(0xFF524344),
    outline = Color(0xFF857374)
)

private val BlossomDark = darkColorScheme(
    primary = Color(0xFFFFB2BB),
    onPrimary = Color(0xFF5F1123),
    primaryContainer = Color(0xFF7E2B3A),
    onPrimaryContainer = Color(0xFFFFD9DE),
    secondary = Color(0xFFE5BDC1),
    onSecondary = Color(0xFF43292C),
    secondaryContainer = Color(0xFF5C3F42),
    onSecondaryContainer = Color(0xFFFFD9DE),
    tertiary = Color(0xFFEDBE93),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF221919),
    onBackground = Color(0xFFF3DDDE),
    surface = Color(0xFF221919),
    onSurface = Color(0xFFF3DDDE),
    surfaceVariant = Color(0xFF524344),
    onSurfaceVariant = Color(0xFFD6C1C2),
    outline = Color(0xFF9F8C8D)
)

private val TerracottaLight = lightColorScheme(
    primary = Color(0xFF8F4C2F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBCC),
    onPrimaryContainer = Color(0xFF380D00),
    secondary = Color(0xFF77574A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDBCC),
    onSecondaryContainer = Color(0xFF2C150C),
    tertiary = Color(0xFF676033),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF221A15),
    surface = Color(0xFFFFF8F5),
    onSurface = Color(0xFF221A15),
    surfaceVariant = Color(0xFFF4DED2),
    onSurfaceVariant = Color(0xFF52443C),
    outline = Color(0xFF85736B)
)

private val TerracottaDark = darkColorScheme(
    primary = Color(0xFFFFB692),
    onPrimary = Color(0xFF552008),
    primaryContainer = Color(0xFF74361D),
    onPrimaryContainer = Color(0xFFFFDBCC),
    secondary = Color(0xFFE7BEAD),
    onSecondary = Color(0xFF442A1E),
    secondaryContainer = Color(0xFF5D4033),
    onSecondaryContainer = Color(0xFFFFDBCC),
    tertiary = Color(0xFFD0C790),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF221A15),
    onBackground = Color(0xFFF4DED2),
    surface = Color(0xFF221A15),
    onSurface = Color(0xFFF4DED2),
    surfaceVariant = Color(0xFF52443C),
    onSurfaceVariant = Color(0xFFD7C3B7),
    outline = Color(0xFF9F8D83)
)

fun colorSchemeFor(theme: AppTheme, darkTheme: Boolean) = when (theme) {
    AppTheme.HERBARIUM -> if (darkTheme) HerbariumDark else HerbariumLight
    AppTheme.FOREST -> if (darkTheme) ForestDark else ForestLight
    AppTheme.BLOSSOM -> if (darkTheme) BlossomDark else BlossomLight
    AppTheme.TERRACOTTA -> if (darkTheme) TerracottaDark else TerracottaLight
}
