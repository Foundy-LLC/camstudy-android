package io.foundy.crop.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.crop.data.api.CropApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CropNetworkModule {

    @Singleton
    @Provides
    fun providesCropApi(retrofit: Retrofit): CropApi {
        return retrofit.create(CropApi::class.java)
    }
}
