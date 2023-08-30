package com.dev.data.storage.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class RoomRealm : RealmObject {
    var name: String = ""
    var cameras: RealmList<Camera> = realmListOf()
}

class Camera : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var snapshotLink: String = ""
    var roomName: String? = ""
    var isFavorite: Boolean = true
    var isRec: Boolean = true
}