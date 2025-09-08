package com.example.satellitetracker.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.satellitetracker.domain.model.Satellite
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.domain.usecase.GetSatellitesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ListIntent {
    data object LoadSatellites : ListIntent
    data class SearchQueryChanged(val query: String) : ListIntent
    data object Refresh : ListIntent
}

data class ListUiState(
    val isLoading: Boolean = false,
    val satellites: List<Satellite> = emptyList(),
    val filteredSatellites: List<Satellite> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ListViewModel @Inject constructor(
    private val getSatellitesUseCase: GetSatellitesUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch(dispatcherProvider.io) {
            processIntent(ListIntent.LoadSatellites)
        }

        viewModelScope.launch(dispatcherProvider.io) {
            _searchQuery
                .debounce(300)
                .collect(::filterSatellites)
        }
    }

    fun processIntent(intent: ListIntent) {
        when (intent) {
            is ListIntent.LoadSatellites -> loadSatellites()
            is ListIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            is ListIntent.Refresh -> refreshSatellites()
        }
    }

    private fun loadSatellites() {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }
            
            try {
                val satellites = getSatellitesUseCase()
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        satellites = satellites,
                        filteredSatellites = satellites,
                        errorMessage = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "Failed to load satellites: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun refreshSatellites() {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiState.update { state -> state.copy(isRefreshing = true, errorMessage = null) }
            
            try {
                val satellites = getSatellitesUseCase()
                val currentQuery = _uiState.value.searchQuery
                val filteredSatellites = if (currentQuery.isBlank()) {
                    satellites
                } else {
                    satellites.filter { satellite ->
                        satellite.name.contains(currentQuery, ignoreCase = true)
                    }
                }
                
                _uiState.update { state ->
                    state.copy(
                        isRefreshing = false,
                        satellites = satellites,
                        filteredSatellites = filteredSatellites,
                        errorMessage = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isRefreshing = false,
                        errorMessage = "Failed to refresh satellites: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.update { state -> state.copy(searchQuery = query) }
    }

    private fun filterSatellites(query: String) {
        val currentSatellites = _uiState.value.satellites
        val filteredSatellites = if (query.isBlank()) {
            currentSatellites
        } else {
            currentSatellites.filter { satellite ->
                satellite.name.contains(query, ignoreCase = true)
            }
        }
        
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredSatellites = filteredSatellites
            ) 
        }
    }
}
