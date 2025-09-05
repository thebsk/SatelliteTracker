package com.example.satellitetracker.core.domain.repository

import com.example.satellitetracker.data.model.Satellite
import com.example.satellitetracker.data.model.SatelliteDetail

interface SatelliteRepository {
    suspend fun getSatellites(): List<Satellite>
    suspend fun getSatelliteDetail(id: Int): SatelliteDetail?
}
