package com.roemerplant.app.data

import com.roemerplant.app.data.ai.PlantAnalysis
import com.roemerplant.app.data.db.PlantDao
import com.roemerplant.app.data.model.Plant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepository @Inject constructor(
    private val plantDao: PlantDao
) {
    fun observeAll(): Flow<List<Plant>> = plantDao.observeAll()

    fun observeById(id: Long): Flow<Plant?> = plantDao.observeById(id)

    suspend fun save(plant: Plant): Long =
        if (plant.id == 0L) plantDao.insert(plant) else {
            plantDao.update(plant)
            plant.id
        }

    suspend fun delete(plant: Plant) = plantDao.delete(plant)

    fun applyAnalysis(plant: Plant, analysis: PlantAnalysis): Plant = plant.copy(
        species = listOf(analysis.species, analysis.commonName)
            .filter { it.isNotBlank() }
            .joinToString(" — "),
        description = analysis.description,
        careLight = analysis.care.light,
        careWater = analysis.care.water,
        careHumidity = analysis.care.humidity,
        careTemperature = analysis.care.temperature,
        careFertilizer = analysis.care.fertilizer,
        careSoil = analysis.care.soil,
        healthStatus = analysis.health.status,
        healthAnalysis = analysis.health.analysis,
        healthInstructions = analysis.health.instructions.joinToString("\n"),
        lastAnalyzedAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}
