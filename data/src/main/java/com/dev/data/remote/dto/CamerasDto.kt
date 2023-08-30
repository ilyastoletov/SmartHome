package com.dev.data.remote.dto

import com.dev.data.remote.dto.CameraDto.Companion.toCamerasList
import com.dev.domain.model.Camera
import com.dev.domain.model.Room

data class CamerasDto(
    val success: Boolean,
    val data: CamerasData
) {
    companion object {
        fun CamerasDto.toRooms(): List<Room> {
            val roomsList: ArrayList<Room> = arrayListOf()
            for (room in this.data.room) {
                roomsList.add(
                    Room(
                        name = room,
                        cameras = this.data.cameras
                            .toCamerasList()
                            .filter { camera -> camera.roomName == room }
                    )
                )
            }
            return roomsList
        }
    }
}

data class CamerasData(
    val room: Array<String>,
    val cameras: Array<CameraDto>
)

data class CameraDto(
    val name: String,
    val snapshot: String,
    val room: String?,
    val id: Int,
    val favorites: Boolean,
    val rec: Boolean
) {
    companion object {
        fun Array<CameraDto>.toCamerasList(): List<Camera> {
            return this.map { cameraDto ->
                Camera(
                    id = cameraDto.id,
                    name = cameraDto.name,
                    snapshotLink = cameraDto.snapshot,
                    roomName = cameraDto.room,
                    isFavorite = cameraDto.favorites,
                    isRec = cameraDto.rec
                )
            }
        }
    }
}