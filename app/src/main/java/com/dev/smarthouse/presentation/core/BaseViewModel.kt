package com.dev.smarthouse.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


interface ViewEvent

interface ViewState

abstract class BaseViewModel<Event: ViewEvent, State: ViewState>
    (protected val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    private val initialState: State by lazy { setInitialState() }
    abstract fun setInitialState(): State

    protected val currentState: State
        get() = state.value

    private val _viewState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state = _viewState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event: SharedFlow<Event> = _event.asSharedFlow()

    init {
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        viewModelScope.launch(dispatcher) {
            event.collect {
                handleEvents(it)
            }
        }
    }

    abstract fun handleEvents(event: Event)

    protected fun setState(reducer: State.() -> State) {
        val newState = state.value.reducer()
        _viewState.value = newState
    }
}