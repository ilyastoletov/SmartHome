package com.dev.domain.usecase

import com.dev.domain.repository.CamerasRepository

class SetCameraFavoriteUseCase(private val repository: CamerasRepository) {
    suspend fun invoke(roomName: String, cameraId: Int, value: Boolean) = repository.cameraSetFavorite(roomName, cameraId, value)
}