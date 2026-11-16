package com.roemerplant.app.util

import android.content.Context
import android.os.Build
import com.roemerplant.app.data.model.Plant
import com.roemerplant.app.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        val now = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).format(Date())
        val pm = context.packageManager
        val pkg = context.packageName
        val versionName = try {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(pkg, 0).versionName ?: "unbekannt"
        } catch (_: Exception) {
            "unbekannt"
        }
        val versionCode = try {
            val info = pm.getPackageInfo(pkg, 0)
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode else info.versionCode.toLong()
        } catch (_: Exception) {
            -1L
        }
        val metrics = context.resources.displayMetrics
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        val densityDpi = metrics.densityDpi

        val withPhoto = plants.count { it.photoUri != null }
        val withAnalysis = plants.count { it.lastAnalyzedAt != null }
        val lastAnalysis = plants.mapNotNull { it.lastAnalyzedAt }.maxOrNull()
        val lastAnalysisText = if (lastAnalysis != null) {
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).format(Date(lastAnalysis))
        } else "noch keine"

        val speciesList = plants.map { it.species }.filter { it.isNotBlank() }.distinct().sorted()

        return buildString {
            appendLine("# PlanzenR Diagnose-Report")
            appendLine()
            appendLine("Erstellt: $now")
            appendLine()
            appendLine("## App")
            appendLine("- Version: $versionName ($versionCode)")
            appendLine("- Paket: `$pkg`")
            appendLine()
            appendLine("## Geraet")
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
            appendLine("_Report automatisch erzeugt von PlanzenR_")
        }.trim()
    }
}
