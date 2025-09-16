package com.example.satellitetracker.domain.usecase

import com.example.satellitetracker.core.result.ApiResult
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.domain.repository.SatelliteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSatelliteDtoDetailUseCaseTest {

    private val repository: SatelliteRepository = mockk()
    private val getSatelliteDetailUseCase = GetSatelliteDetailUseCase(repository)

    @Test
    fun `invoke should return satellite detail from repository`() = runTest {
        val satelliteId = 1
        val satelliteDetail = SatelliteDetail(
            id = satelliteId,
            firstFlight = "2020-01-01",
            costPerLaunch = 1000,
            height = 500,
            mass = 300
        )
        coEvery { repository.getSatelliteDetail(satelliteId) } returns ApiResult.Success(satelliteDetail)

        val result = getSatelliteDetailUseCase(satelliteId)

        assertEquals(satelliteDetail, result.success)
    }
}
