package com.roemerplant.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.roemerplant.app.data.model.Plant

@Database(
    entities = [Plant::class],
    version = 2,
    exportSchema = false
)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE plants ADD COLUMN family TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE plants ADD COLUMN lastWateredAt INTEGER")
                db.execSQL("ALTER TABLE plants ADD COLUMN waterIntervalDays INTEGER NOT NULL DEFAULT 7")
                db.execSQL("ALTER TABLE plants ADD COLUMN lastFertilizedAt INTEGER")
                db.execSQL("ALTER TABLE plants ADD COLUMN fertilizeIntervalDays INTEGER NOT NULL DEFAULT 30")
            }
        }
    }
}
