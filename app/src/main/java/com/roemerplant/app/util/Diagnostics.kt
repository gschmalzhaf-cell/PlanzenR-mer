package com.roemerplant.app.util

import android.content.Context
import android.os.Build
import com.roemerplant.app.BuildConfig
import com.roemerplant.app.data.model.Plant
import com.roemerplant.app.ui.theme.AppTheme

/**
 * Erzeugt einen Diagnose-Report im Markdown-Format.
 * Kann direkt kopiert und weitergegeben werden, um Fehler zu melden
 * oder die App-Qualitaet zu verbessern.
 */
object Diagnostics {

    fun buildReport(
        context: Context,
        theme: AppTheme,
        apiKeySet: Boolean,
        model: String,
        plants: List<Plant>,
        description: String
    ): String {
        val now = DateFormats.formatNowDateTime()
        val pkg = BuildConfig.APPLICATION_ID
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        val metrics = context.resources.displayMetrics
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        val densityDpi = metrics.densityDpi

        val withPhoto = plants.count { it.photoUri != null }
        val withAnalysis = plants.count { it.lastAnalyzedAt != null }
        val lastAnalysis = plants.mapNotNull { it.lastAnalyzedAt }.maxOrNull()
        val lastAnalysisText = if (lastAnalysis != null) {
            DateFormats.formatDateTime(lastAnalysis)
        } else "noch keine"

        val speciesList = plants.map { it.species }.filter { it.isNotBlank() }.distinct().sorted()

        return buildString {
            appendLine("# RömerPlant Diagnose-Report")
            appendLine()
            appendLine("Erstellt: $now")
            appendLine()
            appendLine("## App")
            appendLine("- Version: $versionName ($versionCode)")
            appendLine("- Paket: `$pkg`")
            appendLine()
            appendLine("## Gerät")
            appendLine("- Modell: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("- Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("- Bildschirm: ${screenWidth}x${screenHeight} px, Dichte ${densityDpi} dpi")
            appendLine()
            appendLine("## Konfiguration")
            appendLine("- Farbschema: ${theme.label}")
            appendLine("- Mistral-API-Key: ${if (apiKeySet) "hinterlegt" else "nicht hinterlegt"}")
            appendLine("- KI-Modell: ${model.ifBlank { "Standard" } }")
            appendLine()
            appendLine("## Datenbestand")
            appendLine("- Pflanzen gesamt: ${plants.size}")
            appendLine("- Mit Foto: $withPhoto")
            appendLine("- Mit KI-Analyse: $withAnalysis")
            appendLine("- Letzte KI-Analyse: $lastAnalysisText")
            appendLine("- Erfasste Arten: ${if (speciesList.isEmpty()) "keine" else speciesList.joinToString(", ")}")
            appendLine()
            appendLine("## Fehlerbeschreibung")
            appendLine(description.ifBlank { "_(keine Angabe)_" })
            appendLine()
            appendLine("---")
            appendLine("_Report automatisch erzeugt von RömerPlant_")
        }.trim()
    }
}
