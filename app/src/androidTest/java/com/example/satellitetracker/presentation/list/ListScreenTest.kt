package com.example.satellitetracker.presentation.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.usecase.GetSatellitesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override val default: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val main: CoroutineDispatcher = Dispatchers.Main
    }

    private val getSatellitesUseCase: GetSatellitesUseCase = mockk()

    private lateinit var viewModel: ListViewModel

    private fun createViewModel(): ListViewModel {
        return ListViewModel(getSatellitesUseCase, testDispatcherProvider)
    }

    @Before
    fun setUp() {
        viewModel = createViewModel()

        coEvery { getSatellitesUseCase() } returns emptyList()
    }

    @Test
    fun listScreen_shouldDisplay_satellites() {
        composeTestRule.setContent {
            ListScreen(
                onSatelliteClick = {},
                showSnackBar = {},
                viewModel = viewModel
            )
        }

        val satellites = listOf(
            Satellite(1, "Satellite 1", true),
            Satellite(2, "Satellite 2", false)
        )
        with(viewModel) {
            setState { ListUiState(filteredSatellites = satellites) }
        }

        satellites.forEach { satellite ->
            composeTestRule.onNodeWithText(satellite.name).assertIsDisplayed()
        }
    }

    @Test
    fun listScreen_shouldTriggerCallback_clickingSatelliteItem() {
        val onSatelliteClick: (Int) -> Unit = mockk(relaxed = true)
        composeTestRule.setContent {
            ListScreen(
                onSatelliteClick = onSatelliteClick,
                showSnackBar = {},
                viewModel = viewModel
            )
        }

        val satellite = Satellite(1, "Satellite 1", true)
        with(viewModel) {
            setState { ListUiState(filteredSatellites = listOf(satellite)) }
        }

        composeTestRule.onNodeWithText(satellite.name).performClick()

        verify { onSatelliteClick(satellite.id) }
    }
}
