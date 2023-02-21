package io.foundy.room_list.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.room_list.data.api.RoomListApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomListRetrofitModule {

    @Provides
    @Singleton
    fun providesRoomListApi(retrofit: Retrofit): RoomListApi {
        return retrofit.create(RoomListApi::class.java)
    }
}
