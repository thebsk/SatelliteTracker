package com.example.satellitetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.satellitetracker.domain.model.SatelliteDetail

@Entity(tableName = "satellite_details")
data class SatelliteDetailEntity(
    @PrimaryKey
    val id: Int,
    val costPerLaunch: Long,
    val firstFlight: String,
    val height: Int,
    val mass: Int
)

fun SatelliteDetailEntity.toSatelliteDetail(): SatelliteDetail {
    return SatelliteDetail(
        id = id,
        costPerLaunch = costPerLaunch,
        firstFlight = firstFlight,
        height = height,
        mass = mass
    )
}

fun SatelliteDetail.toSatelliteDetailEntity(): SatelliteDetailEntity {
    return SatelliteDetailEntity(
        id = id,
        costPerLaunch = costPerLaunch,
        firstFlight = firstFlight,
        height = height,
        mass = mass
    )
}
