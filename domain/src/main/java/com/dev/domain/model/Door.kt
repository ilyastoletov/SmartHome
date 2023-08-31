package com.dev.domain.model

data class Door(
    val id: Int,
    val name: String,
    val room: String?,
    val isFavorite: Boolean,
    val snapshotLink: String?
)
