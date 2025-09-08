package com.example.satellitetracker.presentation.mvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface StateDelegate<State : ViewState> {
    val uiState: StateFlow<State>
    fun setState(reducer: State.() -> State)
}

class DefaultStateDelegateImpl<State : ViewState>(
    initialState: State
) : StateDelegate<State> {
    private val _uiState = MutableStateFlow(initialState)
    override val uiState = _uiState.asStateFlow()

    override fun setState(reducer: State.() -> State) {
        _uiState.value = uiState.value.reducer()
    }
}
