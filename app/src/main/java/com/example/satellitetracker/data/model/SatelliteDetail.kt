package com.example.satellitetracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SatelliteDetail(
    val id: Int,
    @SerialName("cost_per_launch")
    val costPerLaunch: Long,
    @SerialName("first_flight")
    val firstFlight: String,
    val height: Int,
    val mass: Int
)
