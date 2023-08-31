package com.dev.domain.usecase.doors

import com.dev.domain.repository.DoorsRepository

class DoorSetNewNameUseCase(private val repository: DoorsRepository) {
    suspend fun invoke(doorId: Int, newName: String) = repository.doorSetNewName(doorId, newName)
}