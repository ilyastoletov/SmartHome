package com.dev.data.remote.dto

import com.dev.domain.model.Door

data class DoorsDto(
    val success: Boolean,
    val data: Array<DoorDto>
) {
    companion object {
        fun DoorsDto.dtoToDoors(): List<Door> {
            return this.data.map { dtoDoor ->
                Door(
                    name = dtoDoor.name,
                    room = dtoDoor.room,
                    isFavorite = dtoDoor.favorites,
                    snapshotLink = dtoDoor.snapshot,
                    id = dtoDoor.id
                )
            }
        }
    }
}

data class DoorDto(
    val name: String,
    val snapshot: String? = null,
    val room: String?,
    val id: Int,
    val favorites: Boolean,
)
