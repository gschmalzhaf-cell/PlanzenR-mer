package com.roemerplant.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.roemerplant.app.data.model.Plant

@Database(
    entities = [Plant::class],
    version = 1,
    exportSchema = false
)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}
