package com.example.satellitetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SatelliteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSatelliteDetail(satelliteDetail: SatelliteDetailEntity)

    @Query("SELECT * FROM satellite_details WHERE id = :id")
    suspend fun getSatelliteDetail(id: Int): SatelliteDetailEntity?
}
