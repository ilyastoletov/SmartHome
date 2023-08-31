package com.dev.data.storage.model

import com.dev.domain.model.Camera
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CameraRealm : RealmObject {
    var id: Int = 0
    @PrimaryKey
    var name: String = ""
    var snapshotLink: String = ""
    var roomName: String? = ""
    var isFavorite: Boolean = true
    var isRec: Boolean = true
}

fun List<Camera>.toCameraRealm(): List<CameraRealm> {
    return this.map { camera -> CameraRealm().apply {
        id = camera.id
        name = camera.name
        snapshotLink = camera.snapshotLink
        roomName = camera.roomName
        isFavorite = camera.isFavorite
        isRec = camera.isRec
    } }
}

fun RealmList<CameraRealm>.toCameras(): List<Camera> {
    return this.map { cameraRealm -> Camera(
        id = cameraRealm.id,
        name = cameraRealm.name,
        snapshotLink = cameraRealm.snapshotLink,
        roomName = cameraRealm.roomName,
        isFavorite = cameraRealm.isFavorite,
        isRec = cameraRealm.isRec
    ) }
}