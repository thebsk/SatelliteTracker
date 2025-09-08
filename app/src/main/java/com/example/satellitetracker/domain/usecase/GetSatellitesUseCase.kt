package com.example.satellitetracker.domain.usecase

import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.repository.SatelliteRepository
import javax.inject.Inject

class GetSatellitesUseCase @Inject constructor(
    private val repository: SatelliteRepository
) {
    suspend operator fun invoke(): List<Satellite> {
        return repository.getSatellites()
    }
}


