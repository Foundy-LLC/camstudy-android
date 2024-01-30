package io.foundy.feature.room.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.MediaRoutingRetrofit
import io.foundy.feature.room.data.api.MediaRoutingApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaRoutingApiModule {

    @Provides
    @Singleton
    fun providesMediaRoutingApi(@MediaRoutingRetrofit retrofit: Retrofit): MediaRoutingApi {
        return retrofit.create(MediaRoutingApi::class.java)
    }
}
