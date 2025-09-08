package com.example.satellitetracker.presentation.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.domain.usecase.GetPositionUpdatesUseCase
import com.example.satellitetracker.domain.usecase.GetSatelliteDetailUseCase
import com.example.satellitetracker.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSatelliteDetailUseCase: GetSatelliteDetailUseCase = mockk()
    private val getPositionUpdatesUseCase: GetPositionUpdatesUseCase = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private fun createViewModel(): DetailViewModel {
        return DetailViewModel(
            getSatelliteDetailUseCase = getSatelliteDetailUseCase,
            getPositionUpdatesUseCase = getPositionUpdatesUseCase,
            dispatcherProvider = mainDispatcherRule.testDispatcherProvider,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `LoadSatelliteDetail should update state with details and position`() = runTest {
        val satelliteId = 1
        val satelliteDetail = SatelliteDetail(
            id = satelliteId,
            firstFlight = "2020-01-01",
            costPerLaunch = 1000,
            height = 500,
            mass = 300
        )
        val position = Position(1.0, 2.0)
        val positionList = PositionList(id = "1", positions = listOf(position))

        every { savedStateHandle.get<Int>("satelliteId") } returns satelliteId
        coEvery { getSatelliteDetailUseCase(satelliteId) } returns satelliteDetail
        coEvery { getPositionUpdatesUseCase(satelliteId) } returns positionList

        val viewModel = createViewModel()

        with(viewModel) {
            setEvent(DetailEvent.LoadSatelliteDetail)
        }

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(satelliteDetail, state.satelliteDetail)
            assertEquals(position, state.currentPosition)
            assertNull(state.error)
        }
    }

    @Test
    fun `LoadSatelliteDetail should show error when detail fetch fails`() = runTest {
        val satelliteId = 1
        val errorMessage = "Error fetching details"

        every { savedStateHandle.get<Int>("satelliteId") } returns satelliteId
        coEvery { getSatelliteDetailUseCase(satelliteId) } throws RuntimeException(errorMessage)
        coEvery { getPositionUpdatesUseCase(satelliteId) } returns null

        val viewModel = createViewModel()

        with(viewModel) {
            setEvent(DetailEvent.LoadSatelliteDetail)
        }

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Failed to fetch details", state.error)
        }

        viewModel.effect.test {
            assertEquals(DetailEffect.ShowError("Failed to fetch details"), awaitItem())
        }
    }

    @Test
    fun `LoadSatelliteDetail should show error when position fetch fails`() = runTest {
        val satelliteId = 1
        val satelliteDetail = SatelliteDetail(
            id = satelliteId,
            firstFlight = "2020-01-01",
            costPerLaunch = 1000,
            height = 500,
            mass = 300
        )
        val errorMessage = "Error fetching positions"

        every { savedStateHandle.get<Int>("satelliteId") } returns satelliteId
        coEvery { getSatelliteDetailUseCase(satelliteId) } returns satelliteDetail
        coEvery { getPositionUpdatesUseCase(satelliteId) } throws RuntimeException(errorMessage)

        val viewModel = createViewModel()

        with(viewModel) {
            setEvent(DetailEvent.LoadSatelliteDetail)
        }

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(satelliteDetail, state.satelliteDetail)
            assertEquals("Failed to fetch positions", state.error)
        }
        viewModel.effect.test {
            assertEquals(DetailEffect.ShowError("Failed to fetch positions"), awaitItem())
        }
    }
}
