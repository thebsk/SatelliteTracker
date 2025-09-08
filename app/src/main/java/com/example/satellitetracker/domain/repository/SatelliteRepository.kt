package com.example.satellitetracker.domain.repository

import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.model.SatelliteDetail

interface SatelliteRepository {
    suspend fun getSatellites(): List<Satellite>
    suspend fun getSatelliteDetail(id: Int): SatelliteDetail?
    suspend fun getPositions(satelliteId: Int): PositionList?
}
