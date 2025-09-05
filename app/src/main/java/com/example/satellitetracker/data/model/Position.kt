package com.example.satellitetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PositionsResponse(
    val list: List<PositionList>
)

@Serializable
data class PositionList(
    val id: String,
    val positions: List<Position>
)

@Serializable
data class Position(
    val posX: Double,
    val posY: Double
)
