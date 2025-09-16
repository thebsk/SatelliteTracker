package com.example.satellitetracker.presentation.mvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface StateDelegate<State : ViewState> {
    val uiState: StateFlow<State>
    val currentState: State
    fun updateState(reducer: State.() -> State)
}

class DefaultStateDelegateImpl<State : ViewState>(
    initialState: State
) : StateDelegate<State> {
    private val _uiState = MutableStateFlow(initialState)
    override val uiState = _uiState.asStateFlow()
    override val currentState: State
        get() = uiState.value

    override fun updateState(reducer: State.() -> State) =
        _uiState.update { state -> state.reducer() }
}
