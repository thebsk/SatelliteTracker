package com.example.satellitetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SatelliteDetailEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun satelliteDao(): SatelliteDao
}
