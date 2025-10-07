package com.example.satellitetracker.data.mapper

import com.example.satellitetracker.data.dto.PositionDto
import com.example.satellitetracker.data.dto.PositionListDto
import com.example.satellitetracker.data.dto.SatelliteDetailDto
import com.example.satellitetracker.data.dto.SatelliteDto
import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.model.SatelliteDetail

fun SatelliteDto.toDomain(): Satellite {
    return Satellite(
        id = id,
        name = name,
        isActive = active
    )
}

fun SatelliteDetailDto.toDomain(): SatelliteDetail {
    return SatelliteDetail(
        id = id,
        costPerLaunch = costPerLaunch,
        firstFlight = firstFlight,
        height = height,
        mass = mass
    )
}

fun PositionDto.toDomain(): Position {
    return Position(
        posX = posX,
        posY = posY
    )
}

fun PositionListDto.toDomain(): PositionList {
    return PositionList(
        id = id,
        positions = positions.map { it.toDomain() }
    )
}



