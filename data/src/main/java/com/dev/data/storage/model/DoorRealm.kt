package com.dev.data.storage.model

import com.dev.domain.model.Door
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class DoorRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var room: String? = ""
    var isFavorite: Boolean = false
    var snapshotLink: String? = null
}

fun List<DoorRealm>.toDoors(): List<Door> {
    return this.map { doorRealm -> Door(
        name = doorRealm.name,
        room = doorRealm.room,
        isFavorite = doorRealm.isFavorite,
        snapshotLink = doorRealm.snapshotLink,
        id = doorRealm.id
    )
    }
}