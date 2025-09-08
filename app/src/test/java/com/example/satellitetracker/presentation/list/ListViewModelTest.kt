package com.example.satellitetracker.presentation.list

import app.cash.turbine.test
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.usecase.GetSatellitesUseCase
import com.example.satellitetracker.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class ListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSatellitesUseCase: GetSatellitesUseCase = mockk()

    private fun createViewModel(): ListViewModel {
        return ListViewModel(getSatellitesUseCase, mainDispatcherRule.testDispatcherProvider)
    }

    @Test
    fun `loadSatellites should update state with satellites on success`() = runTest {
        val satellites = listOf(
            Satellite(1, "Satellite 1", true),
            Satellite(2, "Satellite 2", false)
        )
        coEvery { getSatellitesUseCase() } returns satellites
        val viewModel = createViewModel()

        with(viewModel) {
            setEvent(ListEvent.LoadSatellites)
        }

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(satellites, state.satellites)
            assertEquals(satellites, state.filteredSatellites)
            assertEquals(null, state.errorMessage)
        }
    }

    @Test
    fun `loadSatellites should update state with error message on failure`() = runTest {
        val errorMessage = "Error loading satellites"
        coEvery { getSatellitesUseCase() } throws RuntimeException(errorMessage)
        val viewModel = createViewModel()

        with(viewModel) {
            setEvent(ListEvent.LoadSatellites)
        }

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.satellites.isEmpty())
            assertTrue(state.filteredSatellites.isEmpty())
            assertEquals("Failed to load satellites: $errorMessage", state.errorMessage)
        }
        viewModel.effect.test {
            assertEquals(ListEffect.ShowError("Failed to load satellites: $errorMessage"), awaitItem())
        }
    }

    @Test
    fun `searchQueryChanged should filter satellites`() = runTest {
        val satellites = listOf(
            Satellite(1, "Starlink-1", true),
            Satellite(2, "Starlink-2", false),
            Satellite(3, "GPS", true)
        )
        coEvery { getSatellitesUseCase() } returns satellites
        val viewModel = createViewModel()

        with(viewModel) {
            setEvent(ListEvent.LoadSatellites)
            setEvent(ListEvent.SearchQueryChanged("Starlink"))
        }

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.filteredSatellites.size)
            assertEquals("Starlink-1", state.filteredSatellites[0].name)
            assertEquals("Starlink-2", state.filteredSatellites[1].name)
        }
    }
}
