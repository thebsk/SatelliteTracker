package com.example.satellitetracker.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.domain.usecase.GetPositionUpdatesUseCase
import com.example.satellitetracker.domain.usecase.GetSatelliteDetailUseCase
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getSatelliteDetailUseCase: GetSatelliteDetailUseCase,
    private val getPositionUpdatesUseCase: GetPositionUpdatesUseCase,
    private val dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateDelegate<DetailUiState> by DefaultStateDelegateImpl(DetailUiState()),
    EventDelegate<DetailEvent> by DefaultEventDelegateImpl(),
    EffectDelegate<DetailEffect> by DefaultEffectDelegateImpl() {

    private val satelliteId: Int = checkNotNull(savedStateHandle["satelliteId"])

    init {
        event
            .onEach(::handleEvent)
            .launchIn(viewModelScope)
    }

    private fun handleEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.LoadSatelliteDetail -> {
                fetchSatelliteDetails()
                startPositionUpdates()
            }
        }
    }

    private fun fetchSatelliteDetails() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val detail = withContext(dispatcherProvider.io) {
                    getSatelliteDetailUseCase(satelliteId)
                }
                setState { copy(isLoading = false, satelliteDetail = detail) }
            } catch (e: Exception) {
                val errorMessage = "Failed to fetch details"
                setState { copy(isLoading = false, error = errorMessage) }
                setEffect(DetailEffect.ShowError(errorMessage))
            }
        }
    }

    private fun startPositionUpdates() {
        viewModelScope.launch {
            try {
                val positions = withContext(dispatcherProvider.io) {
                    getPositionUpdatesUseCase(satelliteId)?.positions.orEmpty()
                }
                positions.asFlow()
                    .collect { position ->
                        setState { copy(currentPosition = position) }
                        delay(3000)
                    }
            } catch (e: Exception) {
                val errorMessage = "Failed to fetch positions"
                setState { copy(error = errorMessage) }
                setEffect(DetailEffect.ShowError(errorMessage))
            }
        }
    }
}
