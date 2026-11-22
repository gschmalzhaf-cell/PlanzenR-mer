package com.roemerplant.app.util

import com.roemerplant.app.data.model.Plant
import java.time.LocalDate
import java.time.Month

data class SmartTip(
    val title: String,
    val tag: String,
    val body: String
)

enum class Season(val label: String) {
    SPRING("Frühling"),
    SUMMER("Sommer"),
    AUTUMN("Herbst"),
    WINTER("Winter")
}

/**
 * Lokale botanische Intelligenz:
 * - Erkennt automatisch Pflege-Intensitäten aus Freitext/KI-Texten
 * - Identifiziert pflegebedürftige Pflanzen für den Garten-Check
 * - Wählt saisonale & sammlungsbezogene Pflegetipps aus
 */
object PlantIntelligence {

    fun currentSeason(date: LocalDate = LocalDate.now()): Season = when (date.month) {
        Month.MARCH, Month.APRIL, Month.MAY -> Season.SPRING
        Month.JUNE, Month.JULY, Month.AUGUST -> Season.SUMMER
        Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> Season.AUTUMN
        Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> Season.WINTER
    }

    fun needsAttention(plant: Plant): Boolean {
        val status = plant.healthStatus.lowercase()
        if (status.isBlank()) return false
        return listOf("krank", "schädling", "pilz", "gestresst", "stress", "kritisch", "schwach")
            .any { status.contains(it) }
    }

    fun plantsNeedingAttention(plants: List<Plant>): List<Plant> =
        plants.filter(::needsAttention)

    fun waterLevel(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.isBlank() -> "Mäßig"
            listOf("viel", "ständig feucht", "gleichmäßig feucht", "nicht austrocknen", "häufig", "täglich")
                .any { lower.contains(it) } -> "Feucht"
            listOf("wenig", "sparsam", "selten", "kaktus", "kaum", "komplett austrocknen", "vollständig austrocknen", "trocken halten")
                .any { lower.contains(it) } -> "Wenig"
            else -> "Mäßig"
        }
    }

    fun lightLevel(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.isBlank() -> "Hell"
            listOf("vollsonnig", "direkte sonne", "sonnig", "viel sonne").any { lower.contains(it) } &&
                !lower.contains("ohne direkte") && !lower.contains("keine direkte") -> "Sonnig"
            listOf("schattig", "schatten", "wenig licht", "dunkel").any { lower.contains(it) } &&
                !lower.contains("halbschatt") -> "Schattig"
            listOf("halbschatt", "absonnig").any { lower.contains(it) } -> "Halbschatten"
            else -> "Hell"
        }
    }

    fun humidityLevel(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.isBlank() -> "Mittel"
            listOf("hoch", "hohe", "tropisch", "besprühen", "60", "70", "80").any { lower.contains(it) } -> "Hoch"
            listOf("niedrig", "gering", "trocken").any { lower.contains(it) } -> "Niedrig"
            else -> "Mittel"
        }
    }

    fun temperatureLevel(text: String): String {
        val lower = text.lowercase()
        if (lower.isBlank()) return "Warm"
        val numbers = Regex("""\d+""").findAll(lower).mapNotNull { it.value.toIntOrNull() }.toList()
        val avg = if (numbers.isNotEmpty()) numbers.average() else null
        return when {
            lower.contains("kühl") || (avg != null && avg < 16.0) -> "Kühl"
            lower.contains("tropisch") || lower.contains("sehr warm") || (avg != null && avg >= 23.0) -> "Warm"
            avg != null && avg < 20.0 -> "Mild"
            else -> "Warm"
        }
    }

    fun dailySmartTip(
        plants: List<Plant>,
        date: LocalDate = LocalDate.now()
    ): SmartTip {
        val attention = plantsNeedingAttention(plants)
        if (attention.isNotEmpty()) {
            val first = attention.first()
            val plantName = first.name.ifBlank { first.species.ifBlank { "Deine Pflanze" } }
            val extra = if (attention.size > 1) " (+${attention.size - 1} weitere)" else ""
            return SmartTip(
                title = "Blick auf $plantName$extra",
                tag = "Garten-Check",
                body = first.healthAnalysis.takeIf { it.isNotBlank() }?.take(140)
                    ?: "Status „${first.healthStatus}“ – prüfe Substrat, Blattunterseiten und Standort oder starte eine neue KI-Analyse."
            )
        }

        val season = currentSeason(date)
        val pool = seasonalTips(season)
        val index = (date.dayOfYear + plants.size) % pool.size
        return pool[index]
    }

    fun seasonalTips(season: Season): List<SmartTip> = when (season) {
        Season.SPRING -> listOf(
            SmartTip(
                title = "Wachstumsstart nutzen",
                tag = "Frühling",
                body = "Die Tage werden länger: Jetzt ist der ideale Zeitpunkt zum Umtopfen und für die erste halbe Düngergabe."
            ),
            SmartTip(
                title = "Blätter sanft abstauben",
                tag = "Frühling",
                body = "Ein feuchtes Tuch entfernt Winterstaub von großen Blättern – so tanken deine Pflanzen die neue Frühlingssonne optimal."
            ),
            SmartTip(
                title = "Stecklinge schneiden",
                tag = "Frühling",
                body = "Frische Triebe bewurzeln im Frühling besonders zuverlässig in Wasser oder lockerem Perlit."
            )
        )
        Season.SUMMER -> listOf(
            SmartTip(
                title = "Mittagssonne abmildern",
                tag = "Sommer",
                body = "Intensive Sommersonne kann zarte Blätter verbrennen – ein leichter Vorhang oder etwas Abstand zum Südfenster schützt."
            ),
            SmartTip(
                title = "Morgens gießen",
                tag = "Sommer",
                body = "Gieße an warmen Tagen früh morgens mit zimmerwarmem Wasser, damit die Wurzeln gut versorgt in den Tag starten."
            ),
            SmartTip(
                title = "Luftfeuchte erhöhen",
                tag = "Sommer",
                body = "Gruppiere Tropenpflanzen nah beieinander – zusammen schaffen sie ihr eigenes angenehmes Mikroklima."
            )
        )
        Season.AUTUMN -> listOf(
            SmartTip(
                title = "Gießrhythmus anpassen",
                tag = "Herbst",
                body = "Mit kürzeren Tagen verdunsten Pflanzen weniger Wasser. Prüfe die Erde tiefer, bevor du erneut gießt."
            ),
            SmartTip(
                title = "Düngen langsam reduzieren",
                tag = "Herbst",
                body = "Bereite deine Sammlung auf die Ruhephase vor und reduziere die Düngung ab Oktober deutlich."
            ),
            SmartTip(
                title = "Helleren Platz suchen",
                tag = "Herbst",
                body = "Rücke lichthungrige Arten näher ans Fenster, damit sie auch bei tief stehender Herbstsonne genug Licht bekommen."
            )
        )
        Season.WINTER -> listOf(
            SmartTip(
                title = "Heizungsluft ausgleichen",
                tag = "Winter",
                body = "Trockene Heizungsluft stresst Tropenpflanzen. Eine Wasserschale auf der Heizung oder Kiesel im Untersetzer hilft."
            ),
            SmartTip(
                title = "Zugluft beim Lüften meiden",
                tag = "Winter",
                body = "Stelle empfindliche Pflanzen beim Stoßlüften kurz aus der kalten Zugluft – Kälteschocks zeigen sich oft erst Tage später."
            ),
            SmartTip(
                title = "Winterruhe respektieren",
                tag = "Winter",
                body = "Im Winter wachsen die meisten Zimmerpflanzen kaum. Sparsam gießen und aufs Düngen verzichten schützt die Wurzeln."
            )
        )
    }
}
