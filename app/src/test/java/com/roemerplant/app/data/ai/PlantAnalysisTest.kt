package com.roemerplant.app.data.ai

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantAnalysisTest {

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    @Test
    fun `analysis parses complete mistral response`() {
        val content = """
            {
              "species": "Monstera deliciosa",
              "commonName": "Fensterblatt",
              "description": "Beliebte Zimmerpflanze mit charakteristischen Blattschlitzen.",
              "care": {
                "light": "Hell, ohne direkte Mittagssonne",
                "water": "Leicht feucht halten, Staunässe vermeiden",
                "humidity": "Mittlere bis hohe Luftfeuchtigkeit",
                "temperature": "18-27 °C",
                "fertilizer": "Alle 4 Wochen im Sommer",
                "soil": "Lockere, humusreiche Erde"
              },
              "health": {
                "status": "gesund",
                "analysis": "Pflanze wirkt vital, keine Schäden erkennbar.",
                "instructions": ["Weiterhin so pflegen"]
              }
            }
        """.trimIndent()

        val analysis = json.decodeFromString<PlantAnalysis>(content)

        assertEquals("Monstera deliciosa", analysis.species)
        assertEquals("Fensterblatt", analysis.commonName)
        assertEquals("hell, ohne direkte Mittagssonne", analysis.care.light.lowercase())
        assertEquals("gesund", analysis.health.status)
        assertTrue(analysis.health.instructions.isNotEmpty())
    }

    @Test
    fun `analysis tolerates missing optional fields`() {
        val content = """{"species": "Unbekannt"}"""
        val analysis = json.decodeFromString<PlantAnalysis>(content)
        assertEquals("Unbekannt", analysis.species)
        assertEquals("", analysis.care.water)
        assertEquals(emptyList<String>(), analysis.health.instructions)
    }
}
