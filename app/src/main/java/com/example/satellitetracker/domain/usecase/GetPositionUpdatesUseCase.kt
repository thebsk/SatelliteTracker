package com.example.satellitetracker.domain.usecase

import com.example.satellitetracker.domain.repository.SatelliteRepository
import javax.inject.Inject

class GetPositionUpdatesUseCase @Inject constructor(
    private val repository: SatelliteRepository
) {
    suspend operator fun invoke(satelliteId: Int) = repository.getPositions(satelliteId)
}
