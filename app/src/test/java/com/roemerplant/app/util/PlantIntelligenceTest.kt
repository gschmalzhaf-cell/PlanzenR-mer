package com.roemerplant.app.util

import com.roemerplant.app.data.model.Plant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PlantIntelligenceTest {

    @Test
    fun `extracts dynamic care levels from German care descriptions`() {
        assertEquals("Wenig", PlantIntelligence.waterLevel("Sparsam gießen, Erde komplett austrocknen lassen"))
        assertEquals("Feucht", PlantIntelligence.waterLevel("Gleichmäßig feucht halten, nicht austrocknen"))
        assertEquals("Mäßig", PlantIntelligence.waterLevel("Gießen wenn die oberste Erdschicht trocken ist"))

        assertEquals("Hell", PlantIntelligence.lightLevel("Hell, ohne direkte Mittagssonne"))
        assertEquals("Sonnig", PlantIntelligence.lightLevel("Vollsonnig am Südfenster"))
        assertEquals("Halbschatten", PlantIntelligence.lightLevel("Halbschattiger bis heller Standort"))
        assertEquals("Schattig", PlantIntelligence.lightLevel("Kommt auch mit schattigen Ecken zurecht"))

        assertEquals("Hoch", PlantIntelligence.humidityLevel("Hohe Luftfeuchtigkeit (60-70%), regelmäßig besprühen"))
        assertEquals("Niedrig", PlantIntelligence.humidityLevel("Niedrige Luftfeuchte völlig ausreichend"))
        assertEquals("Mittel", PlantIntelligence.humidityLevel("Normale Raumluftfeuchtigkeit"))

        assertEquals("Kühl", PlantIntelligence.temperatureLevel("Kühl überwintern bei 10-14 °C"))
        assertEquals("Mild", PlantIntelligence.temperatureLevel("16-19 °C ideal"))
        assertEquals("Warm", PlantIntelligence.temperatureLevel("20-26 °C Zimmertemperatur"))
    }

    @Test
    fun `detects plants needing attention`() {
        val healthy = Plant(id = 1, name = "Monstera", healthStatus = "gesund")
        val stressed = Plant(id = 2, name = "Calathea", healthStatus = "leicht gestresst")
        val sick = Plant(id = 3, name = "Ficus", healthStatus = "Schädlingsbefall")

        assertFalse(PlantIntelligence.needsAttention(healthy))
        assertTrue(PlantIntelligence.needsAttention(stressed))
        assertTrue(PlantIntelligence.needsAttention(sick))
        assertEquals(listOf(stressed, sick), PlantIntelligence.plantsNeedingAttention(listOf(healthy, stressed, sick)))
    }

    @Test
    fun `daily smart tip prioritizes plants needing attention`() {
        val stressed = Plant(
            id = 1,
            name = "Calathea",
            healthStatus = "leicht gestresst",
            healthAnalysis = "Blattränder wirken trocken."
        )
        val tip = PlantIntelligence.dailySmartTip(listOf(stressed), LocalDate.of(2026, 7, 15))
        assertEquals("Garten-Check", tip.tag)
        assertTrue(tip.title.contains("Calathea"))
        assertEquals("Blattränder wirken trocken.", tip.body)
    }

    @Test
    fun `daily smart tip adapts to season when all plants are healthy`() {
        val healthy = Plant(id = 1, name = "Monstera", healthStatus = "gesund")
        val winterTip = PlantIntelligence.dailySmartTip(listOf(healthy), LocalDate.of(2026, 1, 15))
        val summerTip = PlantIntelligence.dailySmartTip(listOf(healthy), LocalDate.of(2026, 7, 15))
        assertEquals("Winter", winterTip.tag)
        assertEquals("Sommer", summerTip.tag)
    }
}
