package com.dev.data.remote.clients

import com.dev.data.remote.dto.CamerasDto
import com.dev.data.remote.dto.DoorsDto
import com.dev.data.utils.NetworkConfig
import retrofit2.http.GET

interface ApiClient {

    @GET(NetworkConfig.CAMERAS)
    suspend fun getCameras(): CamerasDto

    @GET(NetworkConfig.DOORS)
    suspend fun getDoors(): DoorsDto

}