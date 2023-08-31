package com.dev.smarthouse.presentation.screens.cameras.contract

import androidx.lifecycle.viewModelScope
import com.dev.domain.usecase.cameras.GetCamerasListUseCase
import com.dev.domain.usecase.cameras.SetCameraFavoriteUseCase
import com.dev.domain.utils.Response
import com.dev.smarthouse.presentation.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val getCamerasListUseCase: GetCamerasListUseCase,
                                          private val setCameraFavoriteUseCase: SetCameraFavoriteUseCase
)
    : BaseViewModel<CameraContract.Event, CameraContract.State>() {

    override fun setInitialState(): CameraContract.State = CameraContract.State.Loading

    override fun handleEvents(event: CameraContract.Event) = when(event) {
        is CameraContract.Event.LoadCameras -> loadCameras(event.isPullRefresh)
        is CameraContract.Event.SetCamFavorite -> setCameraFavorite(event.roomName, event.cameraId, event.value)
    }

    private fun loadCameras(pullRefresh: Boolean = false) {
        viewModelScope.launch(dispatcher) {
            if (pullRefresh) setState { CameraContract.State.Loading }
            when(val networkResponse = getCamerasListUseCase.invoke(pullRefresh)) {
                is Response.Success.Data -> setState { CameraContract.State.CamerasLoaded(networkResponse.data) }
                is Response.Success.Empty, is Response.Error -> setState { CameraContract.State.NetworkFailure }
            }
        }
    }

    private fun setCameraFavorite(roomName: String, cameraId: Int, value: Boolean) {
        viewModelScope.launch(dispatcher) {
            setCameraFavoriteUseCase.invoke(roomName, cameraId, value)
            loadCameras()
        }
    }

}