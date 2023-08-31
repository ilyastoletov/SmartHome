package com.dev.data.di

import com.dev.data.remote.clients.ApiClient
import com.dev.data.repository.CameraRepoImpl
import com.dev.data.repository.DoorRepoImpl
import com.dev.data.storage.model.CameraRealm
import com.dev.data.storage.model.DoorRealm
import com.dev.data.storage.model.RoomRealm
import com.dev.data.utils.NetworkConfig
import com.dev.domain.repository.CamerasRepository
import com.dev.domain.repository.DoorsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRealmConfig(): RealmConfiguration {
        return RealmConfiguration.create(setOf(RoomRealm::class, CameraRealm::class, DoorRealm::class))
    }

    @Provides
    @Singleton
    fun provideApiClient(retrofit: Retrofit): ApiClient = retrofit.create(ApiClient::class.java)

    @Provides
    @Singleton
    fun provideCameraRepository(apiClient: ApiClient, realmConfiguration: RealmConfiguration): CamerasRepository {
        return CameraRepoImpl(apiClient, realmConfiguration)
    }

    @Provides
    @Singleton
    fun provideDoorsRepository(apiClient: ApiClient, realmConfiguration: RealmConfiguration): DoorsRepository {
        return DoorRepoImpl(apiClient, realmConfiguration)
    }

}