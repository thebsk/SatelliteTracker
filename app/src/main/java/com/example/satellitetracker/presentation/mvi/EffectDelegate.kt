package com.example.satellitetracker.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface EffectDelegate<Effect : ViewEffect> {
    val effect: Flow<Effect>
    fun ViewModel.setEffect(effect: Effect)
}

class DefaultEffectDelegateImpl<Effect : ViewEffect>(
) : EffectDelegate<Effect> {
    private val _effect = Channel<Effect>()
    override val effect = _effect.receiveAsFlow()

    override fun ViewModel.setEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
