package io.foundy.feature.room_list.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.DefaultRetrofit
import io.foundy.core.data.di.RankingRetrofit
import io.foundy.feature.room_list.data.api.RecommendRoomApi
import io.foundy.feature.room_list.data.api.RoomListApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomListRetrofitModule {

    @Provides
    @Singleton
    fun providesRoomListApi(@DefaultRetrofit retrofit: Retrofit): RoomListApi {
        return retrofit.create(RoomListApi::class.java)
    }

    @Provides
    @Singleton
    fun providesRecommendedRoomApi(@RankingRetrofit retrofit: Retrofit): RecommendRoomApi {
        return retrofit.create(RecommendRoomApi::class.java)
    }
}
