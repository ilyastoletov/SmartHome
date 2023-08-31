package com.dev.smarthouse.presentation.di

import com.dev.domain.repository.CamerasRepository
import com.dev.domain.repository.DoorsRepository
import com.dev.domain.usecase.cameras.GetCamerasListUseCase
import com.dev.domain.usecase.cameras.SetCameraFavoriteUseCase
import com.dev.domain.usecase.doors.DoorSetNewNameUseCase
import com.dev.domain.usecase.doors.GetDoorsUseCase
import com.dev.domain.usecase.doors.SetDoorFavoriteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
class DomainModule {

    @Provides
    fun provideGetCamerasListUseCase(camerasRepository: CamerasRepository): GetCamerasListUseCase {
        return GetCamerasListUseCase(camerasRepository)
    }

    @Provides
    fun provideSetCameraFavoriteUseCase(camerasRepository: CamerasRepository): SetCameraFavoriteUseCase {
        return SetCameraFavoriteUseCase(camerasRepository)
    }

    @Provides
    fun provideGetDoorsUseCase(doorsRepository: DoorsRepository): GetDoorsUseCase {
        return GetDoorsUseCase(doorsRepository)
    }

    @Provides
    fun provideSetDoorFavoriteUseCase(doorsRepository: DoorsRepository): SetDoorFavoriteUseCase {
        return SetDoorFavoriteUseCase(doorsRepository)
    }

    @Provides
    fun provideSetDoorNewNameUseCase(doorsRepository: DoorsRepository): DoorSetNewNameUseCase {
        return DoorSetNewNameUseCase(doorsRepository)
    }

}