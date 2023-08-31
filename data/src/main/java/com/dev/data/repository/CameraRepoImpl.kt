package com.dev.data.repository

import android.util.Log
import com.dev.data.remote.clients.ApiClient
import com.dev.data.remote.dto.CamerasDto.Companion.toRooms
import com.dev.data.storage.model.CameraRealm
import com.dev.data.storage.model.RoomRealm
import com.dev.data.storage.model.toCameraRealm
import com.dev.data.storage.model.toRealmRoom
import com.dev.data.storage.model.toRooms
import com.dev.domain.model.Room
import com.dev.domain.repository.CamerasRepository
import com.dev.domain.utils.Response
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.find
import kotlinx.coroutines.flow.collect
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class CameraRepoImpl @Inject constructor(private val apiClient: ApiClient, private val realmConfiguration: RealmConfiguration): CamerasRepository {

    override suspend fun getCamerasList(isPullRefresh: Boolean): Response<List<Room>> {
        return if (isCamerasDatabaseEmpty() || isPullRefresh) {
            loadRoomsByApi()
        } else {
            fetchRoomsFromDatabase()
        }
    }

    override suspend fun cameraSetFavorite(roomName: String, cameraId: Int, value: Boolean) {
        val realm = Realm.open(realmConfiguration)
        realm.write {
            val room = query<RoomRealm>(query = "name == $0", roomName).first().find()
            val cameras = room!!.cameraRealms
            var cameraIndex = 0
            cameras.forEachIndexed { index, cameraRealm -> if(cameraRealm.id == cameraId) cameraIndex = index }
            room.cameraRealms[cameraIndex].isFavorite = value
        }
        realm.close()
    }

    private suspend fun loadRoomsByApi(): Response<List<Room>> {
        return try {
            val networkResponse = apiClient.getCameras()
            val roomsList = networkResponse.toRooms()
            saveRoomsToRealmDatabase(roomsList.toCollection(ArrayList()))
            Response.Success.Data(roomsList)
        } catch(e: HttpException) {
            val message = when(e.code()) {
                403, 401 -> "Сетевая ошибка. Клиент не авторизован"
                404 -> "Сетевая ошибка. Контент не найден"
                500 -> "Серверная ошибка."
                else -> "Неизвестная сетевая ошибка"
            }
            Response.Error(message)
        } catch(e: ConnectException) {
            Response.Error("Ошибка подключения к серверу. Проверьте качество сети")
        } finally {
            Response.Success.Empty
        }
    }

    private fun saveRoomsToRealmDatabase(roomsList: ArrayList<Room>) {
        val realm = Realm.open(realmConfiguration)
        val roomsQuery: RealmResults<RoomRealm> = realm.query(RoomRealm::class).find()

        if (roomsQuery.isEmpty()) {
            realm.writeBlocking {
                roomsList.forEach { room ->
                    copyToRealm(RoomRealm().apply {
                        name = room.name
                        cameraRealms = room.cameras.toCameraRealm().toRealmList()
                    }
                    )
                }
            }
        } else {
            checkAndAddRoomsAndCameras(roomsQuery, roomsList, realm)
        }
        realm.close()
    }

    private fun isCamerasDatabaseEmpty(): Boolean {
        val realm = Realm.open(realmConfiguration)
        val roomsQuery: RealmResults<RoomRealm> = realm.query(RoomRealm::class).find()
        return roomsQuery.isEmpty()
    }

    private fun fetchRoomsFromDatabase(): Response<List<Room>> {
        val realm = Realm.open(realmConfiguration)
        val roomsQuery: RealmResults<RoomRealm> = realm.query(RoomRealm::class).find()
        return Response.Success.Data(roomsQuery.toRooms())
    }

    private fun checkAndAddRoomsAndCameras(roomsQuery: RealmResults<RoomRealm>, roomsList: ArrayList<Room>, realm: Realm) {
        for (roomDb in roomsQuery) {
            for (room in roomsList) {

                if (roomDb.cameraRealms.size < room.cameras.size) {
                    val savingCamerasCount = room.cameras.size - roomDb.cameraRealms.size
                    val camerasList = roomDb.cameraRealms.takeLast(savingCamerasCount)
                    realm.writeBlocking {
                        var roomRealm = realm.query<RoomRealm>("name == $0", room.name).first().find()
                        roomRealm!!.cameraRealms.addAll(camerasList.toRealmList())
                    }
                }

                if (roomsQuery.size < roomsList.size) {
                    val savingRoomsCount = roomsQuery.size - roomsList.size
                    val roomsToSave = roomsList.takeLast(savingRoomsCount)
                    realm.writeBlocking {
                        roomsToSave.toRealmRoom()
                    }
                }
            }
        }
    }

}