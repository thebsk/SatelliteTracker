package com.example.satellitetracker.domain.usecase

import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.repository.SatelliteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSatellitesUseCaseTest {

    private val repository: SatelliteRepository = mockk()
    private val getSatellitesUseCase = GetSatellitesUseCase(repository)

    @Test
    fun `invoke should return satellites from repository`() = runTest {
        val satellites = listOf(
            Satellite(1, "Satellite 1", true),
            Satellite(2, "Satellite 2", false)
        )
        coEvery { repository.getSatellites() } returns satellites

        val result = getSatellitesUseCase()

        assertEquals(satellites, result)
    }
}
