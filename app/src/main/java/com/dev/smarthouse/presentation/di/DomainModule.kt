package com.dev.smarthouse.presentation.di

import com.dev.domain.repository.CamerasRepository
import com.dev.domain.usecase.GetCamerasListUseCase
import com.dev.domain.usecase.SetCameraFavoriteUseCase
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

}