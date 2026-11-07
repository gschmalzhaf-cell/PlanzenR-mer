package com.roemerplant.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roemerplant.app.R

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val epilogue = GoogleFont("Epilogue")
private val quicksand = GoogleFont("Quicksand")
private val nunitoSans = GoogleFont("Nunito Sans")

val EpilogueFamily = FontFamily(
    Font(epilogue, googleFontProvider, weight = FontWeight.Normal),
    Font(epilogue, googleFontProvider, weight = FontWeight.SemiBold),
    Font(epilogue, googleFontProvider, weight = FontWeight.Bold)
)

val QuicksandFamily = FontFamily(
    Font(quicksand, googleFontProvider, weight = FontWeight.Medium),
    Font(quicksand, googleFontProvider, weight = FontWeight.SemiBold),
    Font(quicksand, googleFontProvider, weight = FontWeight.Bold)
)

val NunitoSansFamily = FontFamily(
    Font(nunitoSans, googleFontProvider, weight = FontWeight.Normal),
    Font(nunitoSans, googleFontProvider, weight = FontWeight.Medium),
    Font(nunitoSans, googleFontProvider, weight = FontWeight.SemiBold)
)

val AppTypography = Typography(
    headlineLarge = Typography().headlineLarge.copy(
        fontFamily = EpilogueFamily, fontWeight = FontWeight.Bold
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontFamily = EpilogueFamily, fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = Typography().headlineSmall.copy(
        fontFamily = EpilogueFamily, fontWeight = FontWeight.SemiBold
    ),
    titleLarge = Typography().titleLarge.copy(
        fontFamily = EpilogueFamily, fontWeight = FontWeight.SemiBold
    ),
    titleMedium = Typography().titleMedium.copy(
        fontFamily = EpilogueFamily, fontWeight = FontWeight.SemiBold
    ),
    titleSmall = Typography().titleSmall.copy(
        fontFamily = QuicksandFamily, fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = Typography().bodyLarge.copy(
        fontFamily = NunitoSansFamily, fontWeight = FontWeight.Normal
    ),
    bodyMedium = Typography().bodyMedium.copy(
        fontFamily = NunitoSansFamily, fontWeight = FontWeight.Normal
    ),
    bodySmall = Typography().bodySmall.copy(
        fontFamily = NunitoSansFamily, fontWeight = FontWeight.Normal
    ),
    labelLarge = Typography().labelLarge.copy(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),
    labelMedium = Typography().labelMedium.copy(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),
    labelSmall = Typography().labelSmall.copy(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.4.sp
    )
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)
