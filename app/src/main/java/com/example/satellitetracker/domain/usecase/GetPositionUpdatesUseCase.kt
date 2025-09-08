package com.example.satellitetracker.domain.usecase

import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.repository.SatelliteRepository
import javax.inject.Inject

class GetPositionUpdatesUseCase @Inject constructor(
    private val repository: SatelliteRepository
) {
    suspend operator fun invoke(satelliteId: Int): PositionList? {
        return repository.getPositions(satelliteId)
    }
}
