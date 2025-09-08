package com.example.satellitetracker.domain.usecase

import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.repository.SatelliteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPositionDtoUpdatesUseCaseTest {

    private val repository: SatelliteRepository = mockk()
    private val getPositionUpdatesUseCase = GetPositionUpdatesUseCase(repository)

    @Test
    fun `invoke should return position list from repository`() = runTest {
        val satelliteId = 1
        val positionList = PositionList(
            id = "1",
            positions = listOf(
                Position(1.0, 2.0),
                Position(3.0, 4.0)
            )
        )
        coEvery { repository.getPositions(satelliteId) } returns positionList

        val result = getPositionUpdatesUseCase(satelliteId)

        assertEquals(positionList, result)
    }
}
