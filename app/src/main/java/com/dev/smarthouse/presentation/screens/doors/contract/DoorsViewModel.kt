package com.dev.smarthouse.presentation.screens.doors.contract

import androidx.lifecycle.viewModelScope
import com.dev.domain.usecase.doors.DoorSetNewNameUseCase
import com.dev.domain.usecase.doors.GetDoorsUseCase
import com.dev.domain.usecase.doors.SetDoorFavoriteUseCase
import com.dev.domain.utils.Response
import com.dev.smarthouse.presentation.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoorsViewModel @Inject constructor(private val getDoorsUseCase: GetDoorsUseCase,
                                         private val setDoorFavoriteUseCase: SetDoorFavoriteUseCase,
                                         private val doorSetNewNameUseCase: DoorSetNewNameUseCase) :
    BaseViewModel<DoorsContract.Event, DoorsContract.State>() {

    override fun setInitialState(): DoorsContract.State = DoorsContract.State.Loading

    override fun handleEvents(event: DoorsContract.Event) = when(event) {
        is DoorsContract.Event.LoadDoors -> loadDoors(event.isPullRefresh)
        is DoorsContract.Event.SetDoorFavorite -> setDoorFavorite(event.doorId, event.value)
        is DoorsContract.Event.DoorSetNewName -> doorSetNewName(event.doorId, event.newName)
    }

    private fun loadDoors(isPullRefresh: Boolean) {
        viewModelScope.launch(dispatcher) {
            when(val networkResponse = getDoorsUseCase.invoke(isPullRefresh)) {
                is Response.Success.Data -> setState { DoorsContract.State.DoorsLoaded(networkResponse.data) }
                is Response.Success.Empty, is Response.Error -> setState { DoorsContract.State.NetworkFailure }
            }
        }
    }

    private fun setDoorFavorite(doorId: Int, value: Boolean) {
        viewModelScope.launch(dispatcher) {
            setDoorFavoriteUseCase.invoke(doorId, value)
        }
    }

    private fun doorSetNewName(doorId: Int, newName: String) {
        viewModelScope.launch(dispatcher) {
            doorSetNewNameUseCase.invoke(doorId, newName)
        }
    }

}