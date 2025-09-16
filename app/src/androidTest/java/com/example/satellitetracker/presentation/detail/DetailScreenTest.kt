package com.example.satellitetracker.presentation.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.satellitetracker.core.result.ApiResult
import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.PositionList
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.domain.usecase.GetPositionUpdatesUseCase
import com.example.satellitetracker.domain.usecase.GetSatelliteDetailUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val getSatelliteDetailUseCase: GetSatelliteDetailUseCase = mockk()
    private val getPositionUpdatesUseCase: GetPositionUpdatesUseCase = mockk()
    private lateinit var detailViewModel: DetailViewModel

    private val mockSatelliteDetail = SatelliteDetail(
        id = 1,
        firstFlight = "2020-01-01",
        costPerLaunch = 1000,
        height = 500,
        mass = 300
    )
    private val mockPositionList = PositionList(
        id = "1", positions = listOf(Position(1.0, 2.0))
    )

    private fun createViewModel(): DetailViewModel {
        return spyk(
            DetailViewModel(
                getSatelliteDetailUseCase = getSatelliteDetailUseCase,
                getPositionUpdatesUseCase = getPositionUpdatesUseCase,
                savedStateHandle = mockk {
                    every { get<Int>("satelliteId") } returns 1
                }
            )
        )
    }

    @Before
    fun setUp() {
        coEvery { getSatelliteDetailUseCase.invoke(any()) } returns ApiResult.Success(
            mockSatelliteDetail
        )
        coEvery { getPositionUpdatesUseCase.invoke(any()) } returns ApiResult.Success(
            mockPositionList
        )

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

        val satelliteDetail = mockSatelliteDetail
        val position = mockPositionList.positions.first()

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

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        verify { onBackClick() }
    }
}
