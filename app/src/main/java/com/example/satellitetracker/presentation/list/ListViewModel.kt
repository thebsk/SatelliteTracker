package com.example.satellitetracker.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.domain.usecase.GetSatellitesUseCase
import com.example.satellitetracker.presentation.mvi.DefaultEffectDelegateImpl
import com.example.satellitetracker.presentation.mvi.DefaultEventDelegateImpl
import com.example.satellitetracker.presentation.mvi.DefaultStateDelegateImpl
import com.example.satellitetracker.presentation.mvi.EffectDelegate
import com.example.satellitetracker.presentation.mvi.EventDelegate
import com.example.satellitetracker.presentation.mvi.StateDelegate
import com.example.satellitetracker.presentation.mvi.ViewEffect
import com.example.satellitetracker.presentation.mvi.ViewEvent
import com.example.satellitetracker.presentation.mvi.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@OptIn(FlowPreview::class)
@HiltViewModel
class ListViewModel @Inject constructor(
    private val getSatellitesUseCase: GetSatellitesUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel(),
    StateDelegate<ListUiState> by DefaultStateDelegateImpl(ListUiState()),
    EventDelegate<ListEvent> by DefaultEventDelegateImpl(),
    EffectDelegate<ListEffect> by DefaultEffectDelegateImpl() {

    init {
        event
            .onEach(::handleEvent)
            .launchIn(viewModelScope)

        event
            .filterIsInstance<ListEvent.SearchQueryChanged>()
            .debounce(300)
            .onEach { queryChanged -> filterSatellites(queryChanged.query) }
            .launchIn(viewModelScope)
    }

    private fun handleEvent(event: ListEvent) {
        when (event) {
            is ListEvent.LoadSatellites -> loadSatellites()
            is ListEvent.SearchQueryChanged -> setState { copy(searchQuery = event.query) }
        }
    }

    private fun loadSatellites() {
        viewModelScope.launch(dispatcherProvider.io) {
            setState { copy(isLoading = true, errorMessage = null) }

            try {
                val satellites = getSatellitesUseCase()
                val currentQuery = uiState.value.searchQuery
                val filteredSatellites = if (currentQuery.isBlank()) {
                    satellites
                } else {
                    satellites.filter { satellite ->
                        satellite.name.contains(currentQuery, ignoreCase = true)
                    }
                }

                setState {
                    copy(
                        isLoading = false,
                        satellites = satellites,
                        filteredSatellites = filteredSatellites,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                val errorMessage = "Failed to load satellites: ${e.message}"
                setState {
                    copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
                setEffect(ListEffect.ShowError(errorMessage))
            }
        }
    }

    private fun filterSatellites(query: String) {
        val filteredList = if (query.isBlank()) {
            uiState.value.satellites
        } else {
            uiState.value.satellites.filter { satellite ->
                satellite.name.contains(query, ignoreCase = true)
            }
        }
        setState { copy(filteredSatellites = filteredList) }
    }
}
