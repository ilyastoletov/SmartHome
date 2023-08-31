package com.dev.data.storage.model

import com.dev.domain.model.Room
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject

class RoomRealm : RealmObject {
    var name: String = ""
    var cameraRealms: RealmList<CameraRealm> = realmListOf()
}

fun RealmResults<RoomRealm>.toRooms(): List<Room> {
    return this.map { roomRealm ->
        Room(name = roomRealm.name, cameras = roomRealm.cameraRealms.toCameras())
    }
}

fun List<Room>.toRealmRoom(): List<RoomRealm> {
    return this.map { room ->
        RoomRealm().apply {
            name = room.name
            cameraRealms = room.cameras.toCameraRealm().toRealmList()
        }
    }
}