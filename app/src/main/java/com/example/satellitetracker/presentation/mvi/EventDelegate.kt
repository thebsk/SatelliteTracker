package com.example.satellitetracker.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface EventDelegate<Event : ViewEvent> {
    val event: SharedFlow<Event>
    fun ViewModel.setEvent(event: Event)
}

class DefaultEventDelegateImpl<Event : ViewEvent>(
) : EventDelegate<Event> {
    private val _event = MutableSharedFlow<Event>()
    override val event = _event.asSharedFlow()

    override fun ViewModel.setEvent(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
