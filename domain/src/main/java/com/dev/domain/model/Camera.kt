package com.dev.domain.model

data class Camera(
    val id: Int,
    val name: String,
    val snapshotLink: String,
    val roomName: String?,
    val isFavorite: Boolean,
    val isRec: Boolean
)
