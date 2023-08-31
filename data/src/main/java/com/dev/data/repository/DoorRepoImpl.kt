package com.dev.data.repository

import com.dev.data.remote.clients.ApiClient
import com.dev.data.remote.dto.DoorsDto.Companion.dtoToDoors
import com.dev.data.storage.model.DoorRealm
import com.dev.data.storage.model.RoomRealm
import com.dev.data.storage.model.toDoors
import com.dev.domain.model.Door
import com.dev.domain.repository.DoorsRepository
import com.dev.domain.utils.Response
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.query.RealmResults
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class DoorRepoImpl @Inject constructor(private val apiClient: ApiClient, private val realmConfig: RealmConfiguration): DoorsRepository {

    override suspend fun getDoors(isPullRefresh: Boolean): Response<List<Door>> {
        return if (isDoorsDatabaseEmpty() || isPullRefresh) {
            loadDoorsListFromNetwork()
        } else {
            loadDoorsFromDatabase()
        }
    }

    override suspend fun setDoorFavorite(doorId: Int, value: Boolean) {
        val realm = Realm.open(realmConfig)
        realm.write {
            val door = query(DoorRealm::class, "id = $0", doorId).first().find()
            door!!.isFavorite = value
        }
        realm.close()
    }

    override suspend fun doorSetNewName(doorId: Int, newName: String) {
        val realm = Realm.open(realmConfig)
        realm.write {
            val door = query(DoorRealm::class, "id = $0", doorId).first().find()
            door!!.name = newName
        }
        realm.close()
    }

    private suspend fun loadDoorsListFromNetwork(): Response<List<Door>> {
        return try {
            val networkResponse = apiClient.getDoors()
            val doorsList: List<Door> = networkResponse.dtoToDoors()
            saveDoorsToDatabase(doorsList)
            Response.Success.Data(doorsList)
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

    private fun isDoorsDatabaseEmpty(): Boolean {
        val realm = Realm.open(realmConfig)
        val doorsRealm: RealmResults<DoorRealm> = realm.query(DoorRealm::class).find()
        return doorsRealm.isEmpty()
    }

    private fun loadDoorsFromDatabase(): Response<List<Door>> {
        val realm = Realm.open(realmConfig)
        val doorsRealm: RealmResults<DoorRealm> = realm.query(DoorRealm::class).find()
        return Response.Success.Data(doorsRealm.toDoors())
    }

    private suspend fun saveDoorsToDatabase(doorsList: List<Door>) {
        val realm = Realm.open(realmConfig)
        val databaseDoorsList: RealmResults<DoorRealm> = realm.query(DoorRealm::class).find()

        if (databaseDoorsList.size < doorsList.size) {
            val savingDoorsCount = doorsList.size - databaseDoorsList.size
            val savingDoors = doorsList.takeLast(savingDoorsCount)
            realm.write {
                savingDoors.forEach { door -> copyToRealm(DoorRealm().apply {
                    name = door.name
                    room = door.room
                    isFavorite = door.isFavorite
                    snapshotLink = door.snapshotLink
                    id = door.id
                    }
                )
                }
            }
        }

        realm.close()
    }

}