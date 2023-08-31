package com.dev.domain.repository

import com.dev.domain.model.Door
import com.dev.domain.utils.Response

interface DoorsRepository {
    suspend fun getDoors(isPullRefresh: Boolean = false): Response<List<Door>>
    suspend fun setDoorFavorite(doorId: Int, value: Boolean)
    suspend fun doorSetNewName(doorId: Int, newName: String)
}