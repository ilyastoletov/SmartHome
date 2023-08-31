package com.dev.smarthouse.presentation.screens.doors.contract

import com.dev.domain.model.Door
import com.dev.smarthouse.presentation.core.ViewEvent
import com.dev.smarthouse.presentation.core.ViewState

object DoorsContract {

    sealed class Event : ViewEvent {
        data class LoadDoors(val isPullRefresh: Boolean = false) : Event()
        data class SetDoorFavorite(val doorId: Int, val value: Boolean) : Event()
        data class DoorSetNewName(val doorId: Int, val newName: String) : Event()
    }

    sealed class State : ViewState {
        object Loading : State()
        data class DoorsLoaded(val data: List<Door>) : State()
        object NetworkFailure : State()
    }


}