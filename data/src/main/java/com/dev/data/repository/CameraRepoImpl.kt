package com.dev.data.repository

import com.dev.data.remote.clients.ApiClient
import com.dev.data.remote.dto.CamerasDto.Companion.toRooms
import com.dev.data.storage.model.RoomRealm
import com.dev.domain.model.Room
import com.dev.domain.repository.CamerasRepository
import com.dev.domain.utils.Response
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.query.RealmResults
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class CameraRepoImpl @Inject constructor(private val apiClient: ApiClient, private val realmConfiguration: RealmConfiguration): CamerasRepository {

    override suspend fun getCamerasList(): Response<List<Room>> {
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

    // Сохраняет список комнат в бд и проверяет, не добавились ли дополнительные комнаты или камеры в них
    private fun saveRoomsToRealmDatabase(roomsList: ArrayList<Room>) {
        val realm = Realm.open(realmConfiguration)
        val roomsQuery: RealmResults<RoomRealm> = realm.query(RoomRealm::class).find()

        if (roomsQuery.size == roomsList.size) return

        // TODO(Потестить на Playground)
        for (roomDb in roomsQuery) {
            for (room in roomsList) {
                if (roomDb.cameras.size < room.cameras.size) {
                    val savingCamerasCount = room.cameras.size - roomDb.cameras.size
                    val camerasList = roomDb.cameras.takeLast(savingCamerasCount)
                    val roomObject = roomsList.find { listedRoom -> room.name == listedRoom.name }
                    //val replaceCams = roomObject!!.cameras = camerasList
                }
            }
        }
    }

}