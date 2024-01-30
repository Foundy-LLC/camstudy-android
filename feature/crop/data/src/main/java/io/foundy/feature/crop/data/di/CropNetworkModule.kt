package io.foundy.feature.crop.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.DefaultRetrofit
import io.foundy.feature.crop.data.api.CropApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CropNetworkModule {

    @Singleton
    @Provides
    fun providesCropApi(@DefaultRetrofit retrofit: Retrofit): CropApi {
        return retrofit.create(CropApi::class.java)
    }
}
