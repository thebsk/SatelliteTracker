package com.example.satellitetracker.core.use_cases

import com.example.satellitetracker.core.domain.repository.SatelliteRepository
import com.example.satellitetracker.data.model.Satellite
import javax.inject.Inject

class GetSatellitesUseCase @Inject constructor(
    private val repository: SatelliteRepository
) {
    suspend operator fun invoke(): List<Satellite> {
        return repository.getSatellites()
    }
}
