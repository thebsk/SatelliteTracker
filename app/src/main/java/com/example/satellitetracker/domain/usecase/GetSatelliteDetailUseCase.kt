package com.example.satellitetracker.domain.usecase

import com.example.satellitetracker.domain.repository.SatelliteRepository
import javax.inject.Inject

class GetSatelliteDetailUseCase @Inject constructor(
    private val repository: SatelliteRepository
) {
    suspend operator fun invoke(id: Int) = repository.getSatelliteDetail(id)
}
