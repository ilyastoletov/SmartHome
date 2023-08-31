package com.dev.domain.usecase

import com.dev.domain.repository.CamerasRepository

class GetCamerasListUseCase(private val repository: CamerasRepository) {
    suspend fun invoke(isPullRefresh: Boolean) = repository.getCamerasList(isPullRefresh)
}