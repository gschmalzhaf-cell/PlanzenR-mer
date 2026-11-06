package com.roemerplant.app.data.ai

import kotlinx.serialization.Serializable

@Serializable
data class PlantAnalysis(
    val species: String = "",
    val commonName: String = "",
    val family: String = "",
    val description: String = "",
    val care: CareInstructions = CareInstructions(),
    val health: HealthAssessment = HealthAssessment()
)

@Serializable
data class CareInstructions(
    val light: String = "",
    val water: String = "",
    val humidity: String = "",
    val temperature: String = "",
    val fertilizer: String = "",
    val soil: String = ""
)

@Serializable
data class HealthAssessment(
    val status: String = "unbekannt",
    val analysis: String = "",
    val instructions: List<String> = emptyList()
)
