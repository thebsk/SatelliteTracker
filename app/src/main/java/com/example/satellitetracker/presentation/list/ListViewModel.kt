package com.example.satellitetracker.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.satellitetracker.core.result.ApiResult
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.presentation.ErrorMessageProvider
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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
    private val dispatcherProvider: DispatcherProvider,
    private val errorMessageProvider: ErrorMessageProvider
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
            .map { queryChanged ->
                currentState.satellites.filterSatellites(queryChanged.query)
            }
            .flowOn(dispatcherProvider.io)
            .onEach { filteredSatellites ->
                updateState { copy(filteredSatellites = filteredSatellites) }
            }
            .launchIn(viewModelScope)
    }

    private fun handleEvent(event: ListEvent) {
        when (event) {
            is ListEvent.LoadSatellites -> loadSatellites()
            is ListEvent.SearchQueryChanged -> updateState { copy(searchQuery = event.query) }
        }
    }

    private fun loadSatellites() {
        viewModelScope.launch(dispatcherProvider.io) {
            updateState { copy(isLoading = true, errorMessage = null) }

            when (val result = getSatellitesUseCase()) {
                is ApiResult.Success -> {
                    val satelliteList: List<Satellite> = result.data
                    val currentQuery = uiState.value.searchQuery
                    val filteredSatellites = satelliteList.filterSatellites(currentQuery)

                    updateState {
                        copy(
                            isLoading = false,
                            satellites = satelliteList,
                            filteredSatellites = filteredSatellites,
                            errorMessage = null
                        )
                    }
                }

                is ApiResult.Error -> {
                    val errorMessage = errorMessageProvider.fromFailure(result.error)
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )
                    }
                    setEffect(ListEffect.ShowError(errorMessage))
                }
            }
        }
    }

    private fun List<Satellite>.filterSatellites(query: String): List<Satellite> {
        return if (query.isBlank()) {
            this
        } else {
            this.filter { satellite ->
                satellite.name.contains(query, ignoreCase = true)
            }
        }
    }
}
