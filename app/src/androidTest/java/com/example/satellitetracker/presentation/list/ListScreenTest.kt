package com.example.satellitetracker.presentation.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.satellitetracker.core.result.ApiResult
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.usecase.GetSatellitesUseCase
import com.example.satellitetracker.presentation.ErrorMessageProvider
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
    private val errorMessageProvider: ErrorMessageProvider = mockk()

    private lateinit var viewModel: ListViewModel

    private val mockSatellites = listOf(
        Satellite(1, "Satellite 1", true),
        Satellite(2, "Satellite 2", false)
    )

    private fun createViewModel(): ListViewModel {
        return ListViewModel(
            getSatellitesUseCase = getSatellitesUseCase,
            errorMessageProvider = errorMessageProvider,
            dispatcherProvider = testDispatcherProvider
        )
    }

    @Before
    fun setUp() {
        coEvery { getSatellitesUseCase() } returns ApiResult.Success(mockSatellites)
        viewModel = createViewModel()
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

        mockSatellites.forEach { satellite ->
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

        val satellite = mockSatellites.first()

        composeTestRule.onNodeWithText(satellite.name).performClick()

        verify { onSatelliteClick(satellite.id) }
    }
}
