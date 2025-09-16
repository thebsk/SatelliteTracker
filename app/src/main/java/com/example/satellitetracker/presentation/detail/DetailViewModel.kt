package com.example.satellitetracker.presentation.detail

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.satellitetracker.core.result.ApiResult
import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.domain.usecase.GetPositionUpdatesUseCase
import com.example.satellitetracker.domain.usecase.GetSatelliteDetailUseCase
import com.example.satellitetracker.presentation.ErrorMessageProvider
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
    private val errorMessageProvider: ErrorMessageProvider,
    savedStateHandle: SavedStateHandle,
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

    @VisibleForTesting
    internal fun fetchSatelliteDetails() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            when (val result = getSatelliteDetailUseCase(satelliteId)) {
                is ApiResult.Success -> {
                    val detail = result.data
                    updateState { copy(isLoading = false, satelliteDetail = detail) }
                }

                is ApiResult.Error -> {
                    val errorMessage = errorMessageProvider.fromFailure(result.error)
                    updateState { copy(isLoading = false, error = errorMessage) }
                    setEffect(DetailEffect.ShowError(errorMessage))
                }
            }
        }
    }

    private fun startPositionUpdates() = viewModelScope.launch {
        when (val result = getPositionUpdatesUseCase(satelliteId)) {
            is ApiResult.Success -> {
                result.data?.positions.orEmpty()
                    .asFlow()
                    .onEach { position ->
                        updateState { copy(currentPosition = position) }
                        delay(3000)
                    }
                    .launchIn(this)
            }

            is ApiResult.Error -> {
                val errorMessage = errorMessageProvider.fromFailure(result.error)
                updateState { copy(error = errorMessage) }
                setEffect(DetailEffect.ShowError(errorMessage))
            }
        }
    }
}
