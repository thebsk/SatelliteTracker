package com.example.satellitetracker.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SatelliteDto(
    val id: Int,
    val active: Boolean,
    val name: String
)
