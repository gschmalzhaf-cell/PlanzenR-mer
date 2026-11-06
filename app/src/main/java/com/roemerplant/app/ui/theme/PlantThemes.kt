package com.roemerplant.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppTheme(val label: String) {
    FOREST("Waldgrün"),
    BLOSSOM("Blütenrosa"),
    TERRACOTTA("Terrakotta")
}

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
    AppTheme.FOREST -> if (darkTheme) ForestDark else ForestLight
    AppTheme.BLOSSOM -> if (darkTheme) BlossomDark else BlossomLight
    AppTheme.TERRACOTTA -> if (darkTheme) TerracottaDark else TerracottaLight
}
