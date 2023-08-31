package com.dev.domain.repository

import com.dev.domain.model.Room
import com.dev.domain.utils.Response

interface CamerasRepository {
    suspend fun getCamerasList(isPullRefresh: Boolean = false) : Response<List<Room>>
    suspend fun cameraSetFavorite(roomName: String, cameraId: Int, value: Boolean)
}