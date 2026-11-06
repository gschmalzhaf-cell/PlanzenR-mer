package com.roemerplant.app.di

import android.content.Context
import androidx.room.Room
import com.roemerplant.app.data.db.PlantDao
import com.roemerplant.app.data.db.PlantDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PlantDatabase =
        Room.databaseBuilder(context, PlantDatabase::class.java, "roemerplant.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePlantDao(database: PlantDatabase): PlantDao = database.plantDao()
}
