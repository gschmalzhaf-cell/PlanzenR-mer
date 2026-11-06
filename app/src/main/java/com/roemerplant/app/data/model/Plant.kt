package com.roemerplant.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val species: String = "",
    val family: String = "",
    val photoUri: String? = null,
    val description: String = "",
    val careLight: String = "",
    val careWater: String = "",
    val careHumidity: String = "",
    val careTemperature: String = "",
    val careFertilizer: String = "",
    val careSoil: String = "",
    val healthStatus: String = "",
    val healthAnalysis: String = "",
    val healthInstructions: String = "",
    val lastAnalyzedAt: Long? = null,
    val lastWateredAt: Long? = null,
    val waterIntervalDays: Int = 7,
    val lastFertilizedAt: Long? = null,
    val fertilizeIntervalDays: Int = 30,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
