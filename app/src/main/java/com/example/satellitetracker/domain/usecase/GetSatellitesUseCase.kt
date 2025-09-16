package com.example.satellitetracker.domain.usecase

import com.example.satellitetracker.domain.repository.SatelliteRepository
import javax.inject.Inject

class GetSatellitesUseCase @Inject constructor(
    private val repository: SatelliteRepository,
) {
    suspend operator fun invoke() = repository.getSatellites()
}
