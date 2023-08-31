package com.dev.domain.usecase.doors

import com.dev.domain.repository.DoorsRepository

class SetDoorFavoriteUseCase(private val repository: DoorsRepository) {
    suspend fun invoke(doorId: Int, value: Boolean) = repository.setDoorFavorite(doorId, value)
}