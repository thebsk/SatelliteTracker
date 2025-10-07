package com.example.satellitetracker.data.mapper

import com.example.satellitetracker.data.dto.PositionListDto
import com.example.satellitetracker.data.dto.SatelliteDetailDto
import com.example.satellitetracker.data.dto.SatelliteDto
import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.model.SatelliteDetail
import javax.inject.Inject

class DtoMapper @Inject constructor() {

    fun toDomain(dto: SatelliteDto): Satellite = dto.toDomain()

    fun toDomain(dto: SatelliteDetailDto): SatelliteDetail = dto.toDomain()

    fun toDomain(dto: PositionListDto): PositionList = dto.toDomain()
}



