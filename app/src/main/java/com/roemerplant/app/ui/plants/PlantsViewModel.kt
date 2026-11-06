package com.roemerplant.app.ui.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roemerplant.app.data.PlantRepository
import com.roemerplant.app.data.model.Plant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyCareItem(
    val plant: Plant,
    val waterDue: Boolean,
    val fertilizeDue: Boolean,
    val overdueDays: Long
)

private const val DAY_MS = 24L * 60 * 60 * 1000

private fun waterDue(plant: Plant): Boolean =
    plant.lastWateredAt == null ||
        (System.currentTimeMillis() - plant.lastWateredAt) >= plant.waterIntervalDays * DAY_MS

private fun fertilizeDue(plant: Plant): Boolean =
    plant.lastFertilizedAt == null ||
        (System.currentTimeMillis() - plant.lastFertilizedAt) >= plant.fertilizeIntervalDays * DAY_MS

private fun overdueDays(plant: Plant): Long {
    val last = plant.lastWateredAt ?: return Long.MAX_VALUE
    val overdue = (System.currentTimeMillis() - last) / DAY_MS - plant.waterIntervalDays
    return maxOf(0, overdue)
}

@HiltViewModel
class PlantsViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    val plants: StateFlow<List<Plant>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyCare: StateFlow<List<DailyCareItem>> = repository.observeAll()
        .map { plants ->
            plants.map { plant ->
                DailyCareItem(
                    plant = plant,
                    waterDue = waterDue(plant),
                    fertilizeDue = fertilizeDue(plant),
                    overdueDays = overdueDays(plant)
                )
            }.filter { it.waterDue || it.fertilizeDue }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(plant: Plant) {
        viewModelScope.launch { repository.delete(plant) }
    }

    fun markWatered(plant: Plant) {
        viewModelScope.launch {
            repository.save(plant.copy(lastWateredAt = System.currentTimeMillis()))
        }
    }

    fun markFertilized(plant: Plant) {
        viewModelScope.launch {
            repository.save(plant.copy(lastFertilizedAt = System.currentTimeMillis()))
        }
    }
}
