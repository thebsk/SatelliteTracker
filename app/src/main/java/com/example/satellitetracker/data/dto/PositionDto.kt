package com.example.satellitetracker.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PositionsResponseDto(
    val list: List<PositionListDto>
)

@Serializable
data class PositionListDto(
    val id: String,
    val positions: List<PositionDto>
)

@Serializable
data class PositionDto(
    val posX: Double,
    val posY: Double
)
