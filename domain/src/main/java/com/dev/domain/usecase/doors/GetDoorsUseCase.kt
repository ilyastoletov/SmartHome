package com.dev.domain.usecase.doors

import com.dev.domain.repository.DoorsRepository

class GetDoorsUseCase(private val repository: DoorsRepository) {
    suspend fun invoke(isPullRefresh: Boolean = false) = repository.getDoors(isPullRefresh)
}