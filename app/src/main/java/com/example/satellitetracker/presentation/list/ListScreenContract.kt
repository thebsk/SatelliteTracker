package com.example.satellitetracker.presentation.list

import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.presentation.mvi.ViewEffect
import com.example.satellitetracker.presentation.mvi.ViewEvent
import com.example.satellitetracker.presentation.mvi.ViewState

sealed class ListEvent : ViewEvent {
    data object LoadSatellites : ListEvent()
    data class SearchQueryChanged(val query: String) : ListEvent()
}

data class ListUiState(
    val isLoading: Boolean = false,
    val satellites: List<Satellite> = emptyList(),
    val filteredSatellites: List<Satellite> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null,
) : ViewState

sealed class ListEffect : ViewEffect {
    data class ShowError(val message: String) : ListEffect()
}