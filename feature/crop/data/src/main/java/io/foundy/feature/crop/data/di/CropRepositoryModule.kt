package io.foundy.feature.crop.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.feature.crop.data.repository.CropRepository
import io.foundy.feature.crop.data.repository.NetworkCropRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CropRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsCropRepository(
        networkCropRepository: NetworkCropRepository
    ): CropRepository
}
