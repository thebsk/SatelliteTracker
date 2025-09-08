package com.example.satellitetracker.presentation.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.domain.usecase.GetPositionUpdatesUseCase
import com.example.satellitetracker.domain.usecase.GetSatelliteDetailUseCase
import io.mockk.every
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
class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override val default: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val main: CoroutineDispatcher = Dispatchers.Main
    }

    private val getSatelliteDetailUseCase: GetSatelliteDetailUseCase = mockk()
    private val getPositionUpdatesUseCase: GetPositionUpdatesUseCase = mockk()
    private lateinit var detailViewModel: DetailViewModel

    private fun createViewModel(): DetailViewModel {
        return DetailViewModel(
            getSatelliteDetailUseCase = getSatelliteDetailUseCase,
            getPositionUpdatesUseCase = getPositionUpdatesUseCase,
            dispatcherProvider = testDispatcherProvider,
            savedStateHandle = mockk {
                every { get<Int>("satelliteId") } returns 1
            }
        )
    }

    @Before
    fun setUp() {
        detailViewModel = createViewModel()
    }

    @Test
    fun detailScreen_displaysDetailsAndPosition() {
        composeTestRule.setContent {
            DetailScreen(
                showSnackBar = {},
                onBackClick = {},
                viewModel = detailViewModel
            )
        }

        val satelliteDetail = SatelliteDetail(
            id = 1,
            firstFlight = "2020-01-01",
            costPerLaunch = 1000,
            height = 500,
            mass = 300
        )
        val position = Position(1.0, 2.0)
        with(detailViewModel) {
            setState {
                DetailUiState(
                    isLoading = false,
                    satelliteDetail = satelliteDetail,
                    currentPosition = position
                )
            }
        }

        composeTestRule.onNodeWithText(satelliteDetail.firstFlight).assertIsDisplayed()
        composeTestRule.onNodeWithText("${satelliteDetail.height}/${satelliteDetail.mass}")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(satelliteDetail.costPerLaunch.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithText("(${position.posX}, ${position.posY})").assertIsDisplayed()
    }

    @Test
    fun detailScreen_backButtonClick_triggersCallback() {
        val onBackClick: () -> Unit = mockk(relaxed = true)
        composeTestRule.setContent {
            DetailScreen(
                showSnackBar = {},
                onBackClick = onBackClick,
                viewModel = detailViewModel
            )
        }

        with(detailViewModel) {
            setState { DetailUiState(isLoading = false) }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        verify { onBackClick() }
    }
}
