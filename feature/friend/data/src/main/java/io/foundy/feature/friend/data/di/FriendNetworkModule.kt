package io.foundy.feature.friend.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.DefaultRetrofit
import io.foundy.core.data.di.RankingRetrofit
import io.foundy.feature.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.feature.friend.data.api.FriendApi
import io.foundy.feature.friend.data.api.RecommendUserApi
import io.foundy.feature.friend.data.repository.FriendRepository
import io.foundy.feature.friend.data.repository.NetworkFriendRepository
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
    fun providesRecommendApi(@RankingRetrofit retrofit: Retrofit): RecommendUserApi {
        return retrofit.create(RecommendUserApi::class.java)
    }

    @Provides
    @Singleton
    fun providesFriendRepository(
        api: FriendApi,
        getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
        recommendUserApi: RecommendUserApi
    ): FriendRepository {
        return NetworkFriendRepository(
            friendApi = api,
            getCurrentUserIdUseCase = getCurrentUserIdUseCase,
            recommendUserApi = recommendUserApi
        )
    }
}
