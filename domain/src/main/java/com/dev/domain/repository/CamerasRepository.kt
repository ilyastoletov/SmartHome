package com.dev.domain.repository

import com.dev.domain.model.Room
import com.dev.domain.utils.Response

interface CamerasRepository {
    suspend fun getCamerasList() : Response<List<Room>>
}