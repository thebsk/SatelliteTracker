package com.example.satellitetracker.domain.repository

import com.example.satellitetracker.core.result.ApiResult
import com.example.satellitetracker.core.result.Failure
import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.model.SatelliteDetail

interface SatelliteRepository {
    suspend fun getSatellites(): ApiResult<Failure, List<Satellite>>
    suspend fun getSatelliteDetail(id: Int): ApiResult<Failure, SatelliteDetail?>
    suspend fun getPositions(satelliteId: Int): ApiResult<Failure, PositionList?>
}
