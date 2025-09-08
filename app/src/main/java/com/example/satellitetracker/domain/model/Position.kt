package com.example.satellitetracker.domain.model

data class Position(
    val posX: Double,
    val posY: Double
)

data class PositionList(
    val id: String,
    val positions: List<Position>
)
