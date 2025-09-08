package com.example.satellitetracker.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.SatelliteDetail
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.usecase.GetSatelliteDetailUseCase
import com.example.satellitetracker.domain.usecase.GetPositionUpdatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val isLoading: Boolean = true,
    val satelliteDetail: SatelliteDetail? = null,
    val currentPosition: Position? = null,
    val error: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getSatelliteDetailUseCase: GetSatelliteDetailUseCase,
    private val getPositionUpdatesUseCase: GetPositionUpdatesUseCase,
    private val dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val satelliteId: Int = checkNotNull(savedStateHandle["satelliteId"])

    init {
        fetchSatelliteDetails()
        startPositionUpdates()
    }

    private fun fetchSatelliteDetails() {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val detail = getSatelliteDetailUseCase(satelliteId)
                _uiState.update { it.copy(isLoading = false, satelliteDetail = detail) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to fetch details") }
            }
        }
    }

    private fun startPositionUpdates() {
        viewModelScope.launch(dispatcherProvider.io) {
            try {
                val positionList = getPositionUpdatesUseCase(satelliteId)
                positionList?.positions?.let { positions ->
                    flow {
                        for (position in positions) {
                            emit(position)
                            delay(3000)
                        }
                    }.flowOn(dispatcherProvider.io)
                    .collect { position ->
                        _uiState.update { it.copy(currentPosition = position) }
                    }
                }
            } catch (e: Exception) {
                 _uiState.update { it.copy(error = "Failed to fetch positions") }
            }
        }
    }
}
