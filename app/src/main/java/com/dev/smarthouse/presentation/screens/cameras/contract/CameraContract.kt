package com.dev.smarthouse.presentation.screens.cameras.contract

import com.dev.domain.model.Room
import com.dev.smarthouse.presentation.core.ViewEffect
import com.dev.smarthouse.presentation.core.ViewEvent
import com.dev.smarthouse.presentation.core.ViewState

object CameraContract {

    sealed class Event : ViewEvent {
        data class LoadCameras(val isPullRefresh: Boolean = false) : Event()
        data class SetCamFavorite(val roomName: String, val cameraId: Int, val value: Boolean) : Event()
    }

    sealed class State : ViewState {
        object Loading : State()
        data class CamerasLoaded(val data: List<Room>) : State()
        object NetworkFailure : State()
    }

    sealed class Effect : ViewEffect {}

}