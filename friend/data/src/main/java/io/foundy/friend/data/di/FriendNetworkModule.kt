package io.foundy.friend.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.data.di.DefaultRetrofit
import io.foundy.core.data.di.RankingRetrofit
import io.foundy.friend.data.api.FriendApi
import io.foundy.friend.data.api.RecommendApi
import io.foundy.friend.data.repository.FriendRepository
import io.foundy.friend.data.repository.NetworkFriendRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FriendNetworkModule {

    @Provides
    @Singleton
    fun providesFriendApiService(@DefaultRetrofit retrofit: Retrofit): FriendApi {
        return retrofit.create(FriendApi::class.java)
    }

    @Provides
    @Singleton
    fun providesRecommendApi(@RankingRetrofit retrofit: Retrofit): RecommendApi {
        return retrofit.create(RecommendApi::class.java)
    }

    @Provides
    @Singleton
    fun providesFriendRepository(
        api: FriendApi,
        authRepository: AuthRepository,
        recommendApi: RecommendApi
    ): FriendRepository {
        return NetworkFriendRepository(
            friendApi = api,
            authRepository = authRepository,
            recommendApi = recommendApi
        )
    }
}
