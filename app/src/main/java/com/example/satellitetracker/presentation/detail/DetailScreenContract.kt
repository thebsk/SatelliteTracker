package com.example.satellitetracker.presentation.detail

import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.presentation.mvi.ViewEffect
import com.example.satellitetracker.presentation.mvi.ViewEvent
import com.example.satellitetracker.presentation.mvi.ViewState

sealed class DetailEvent : ViewEvent {
    data object LoadSatelliteDetail : DetailEvent()
}

data class DetailUiState(
    val isLoading: Boolean = true,
    val satelliteDetail: SatelliteDetail? = null,
    val currentPosition: Position? = null,
    val error: String? = null
) : ViewState

sealed class DetailEffect : ViewEffect {
    data class ShowError(val message: String) : DetailEffect()
}
